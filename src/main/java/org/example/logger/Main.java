package org.example.logger;

public class Main {

    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static final ConsoleAppender consoleAppender = new ConsoleAppender("consoleAppender", Level.DEBUG);

    public static void main(String[] args) throws InterruptedException {
        LOGGER.info("This is an info message");
        LOGGER.error("This is an error message");
        LOGGER.debug("This is an debug message");
        LOGGER.fatal("This is an fatal message");
        Thread.sleep(10000);
    }
}
