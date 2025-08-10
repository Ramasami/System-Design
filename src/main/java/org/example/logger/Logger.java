package org.example.logger;

import lombok.AllArgsConstructor;

import java.util.Date;

/**
 * A logging utility class that provides methods for logging messages at different severity levels.
 * This class acts as a wrapper around the LogManager singleton to provide class-specific logging.
 *
 * Example usage:
 * <pre>
 * Logger logger = new Logger(MyClass.class);
 * logger.info("Processing started");
 * logger.error("Failed to connect to database");
 * </pre>
 *
 * The logger supports the following log levels in ascending order of severity:
 * - DEBUG: Detailed information for debugging
 * - INFO: General information about program execution
 * - WARN: Potentially harmful situations
 * - ERROR: Error events that might still allow the application to continue
 * - FATAL: Severe errors that prevent the application from continuing
 */
@AllArgsConstructor
public class Logger {
    /** The class associated with this logger instance */
    private final Class<?> clazz;

    /**
     * Logs a message with the specified severity level.
     *
     * Example:
     * <pre>
     * logger.log(Level.INFO, "User authentication successful");
     * </pre>
     *
     * @param level the severity level of the log message
     * @param message the message to be logged
     */
    public void log(ILevel level, String message) {
        LogManager.getInstance().appendMessage(new Message(new Date(), message, clazz, level));
    }

    /**
     * Logs a debug message.
     * Use this for detailed information for debugging purposes.
     *
     * Example:
     * <pre>
     * logger.debug("Request parameters: " + params);
     * </pre>
     *
     * @param message the debug message to be logged
     */
    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    /**
     * Logs an informational message.
     * Use this for general information about program execution.
     *
     * Example:
     * <pre>
     * logger.info("Application started successfully");
     * </pre>
     *
     * @param message the information message to be logged
     */
    public void info(String message) {
        log(Level.INFO, message);
    }

    /**
     * Logs a warning message.
     * Use this for potentially harmful situations.
     *
     * Example:
     * <pre>
     * logger.warn("Configuration file not found, using defaults");
     * </pre>
     *
     * @param message the warning message to be logged
     */
    public void warn(String message) {
        log(Level.WARN, message);
    }

    /**
     * Logs an error message.
     * Use this for error events that might still allow the application to continue.
     *
     * Example:
     * <pre>
     * logger.error("Failed to save user preferences");
     * </pre>
     *
     * @param message the error message to be logged
     */
    public void error(String message) {
        log(Level.ERROR, message);
    }

    /**
     * Logs a fatal message.
     * Use this for severe errors that prevent the application from continuing.
     *
     * Example:
     * <pre>
     * logger.fatal("Database connection failed - shutting down");
     * </pre>
     *
     * @param message the fatal error message to be logged
     */
    public void fatal(String message) {
        log(Level.FATAL, message);
    }
}