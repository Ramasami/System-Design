package org.example.logger;

import lombok.Getter;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A singleton class that manages the asynchronous processing and distribution of log messages
 * to registered appenders. This class serves as the central hub for the logging system,
 * handling message queuing, processing, and distribution to appropriate log appenders.
 *
 * Features:
 * - Asynchronous message processing
 * - Configurable message queue capacity
 * - Batch processing of messages
 * - Support for multiple log appenders
 * - Level-based message filtering
 *
 * Example usage:
 * <pre>
 * LogManager manager = LogManager.getInstance();
 * manager.registerNewConsumer(new ConsoleAppender(), Level.INFO);
 * manager.appendMessage(new Message(new Date(), "Test message", TestClass.class, Level.INFO));
 * </pre>
 */
public class LogManager implements AutoCloseable {

    /** Maximum number of messages that can be queued for processing */
    private static final int QUEUE_CAPACITY = 10_000;

    /** Maximum time to wait for message processing during shutdown (in milliseconds) */
    private static final long SHUTDOWN_TIMEOUT_MS = 5000;

    /** Singleton instance of the LogManager */
    @Getter
    private static final LogManager instance = new LogManager();

    /** Map of registered log appenders and their minimum log levels */
    private final Map<LogAppender, ILevel> consumers = new HashMap<>();

    /** Queue for holding messages waiting to be processed */
    private final BlockingQueue<Message> messageQueue;

    /** Executor service for asynchronous message processing */
    private final ExecutorService processingExecutor;

    /** Flag indicating whether the manager is running */
    private final AtomicBoolean isRunning;

    /**
     * Private constructor to enforce singleton pattern.
     * Initializes the message queue, processing executor, and starts message processing.
     */
    private LogManager() {
        this.messageQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
        this.processingExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "LogManager-Processor");
            thread.setDaemon(true);
            return thread;
        });
        this.isRunning = new AtomicBoolean(true);
        startMessageProcessor();
    }

    /**
     * Registers a new log appender with its minimum log level.
     *
     * @param consumer the log appender to register
     * @param level the minimum level of messages to be sent to this appender
     */
    public void registerNewConsumer(LogAppender consumer, ILevel level) {
        consumers.put(consumer, level);
    }

    /**
     * Unregisters a previously registered log appender.
     *
     * @param consumer the log appender to unregister
     */
    public void unregisterNewConsumer(LogAppender consumer) {
        consumers.remove(consumer);
    }

    /**
     * Adds a new message to the processing queue.
     * If the queue is full, the message will be dropped.
     *
     * @param message the log message to be processed
     */
    public synchronized void appendMessage(Message message) {
        messageQueue.offer(message);
    }

    /**
     * Starts the message processing thread that continuously processes messages
     * from the queue and distributes them to registered appenders.
     * This method is called during the initialization of the LogManager.
     */
    private void startMessageProcessor() {
        processingExecutor.submit(() -> {
            while (isRunning.get() || !messageQueue.isEmpty()) {
                try {
                    processMessages();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // Log error to system error stream as last resort
                    System.err.println("Error processing log messages: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Processes messages from the queue and distributes them to registered appenders.
     * Messages are filtered based on their log level before being sent to each appender.
     *
     * This method is called by the processing executor in a loop until the manager is closed.
     *
     * @throws InterruptedException if the thread is interrupted while waiting for messages
     */
    private void processMessages() throws InterruptedException {
        List<Message> messages = new ArrayList<>();
        Message message = messageQueue.poll(100, TimeUnit.MILLISECONDS);

        if (message != null) {
            messages.add(message);
            messageQueue.drainTo(messages, 100); // Batch process up to 100 messages

            for (Map.Entry<LogAppender, ILevel> entry : consumers.entrySet()) {
                try {
                    LogAppender appender = entry.getKey();
                    ILevel minimumLevel = entry.getValue();

                    List<Message> filteredMessages = messages.stream()
                            .filter(msg -> msg.level().getPriority() >= minimumLevel.getPriority())
                            .toList();

                    if (!filteredMessages.isEmpty()) {
                        appender.append(filteredMessages);
                    }
                } catch (Exception e) {
                    System.err.println("Error in appender " + entry.getKey() + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Shuts down the LogManager, ensuring all pending messages are processed
     * and appenders are properly closed.
     */
    @Override
    public void close() {
        isRunning.set(false);
        try {
            processingExecutor.shutdown();
            if (!processingExecutor.awaitTermination(SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
                processingExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            processingExecutor.shutdownNow();
        }

        consumers.keySet().forEach(appender -> {
            try {
                appender.close();
            } catch (Exception e) {
                System.err.println("Error closing appender: " + e.getMessage());
            }
        });
    }

    /**
     * Returns the current number of messages waiting to be processed.
     *
     * @return the size of the message queue
     */
    public int getQueueSize() {
        return messageQueue.size();
    }

    /**
     * Returns the number of registered log appenders.
     *
     * @return the number of appenders
     */
    public int getAppenderCount() {
        return consumers.size();
    }
}