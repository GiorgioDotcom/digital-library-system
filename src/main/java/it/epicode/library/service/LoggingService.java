package it.epicode.library.service;

import java.util.logging.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class LoggingService {
    private static final String LOGGER_NAME = "LibrarySystem";
    private static LoggingService instance;
    private Logger rootLogger;

    private LoggingService() {
        setupLogging();
    }

    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    /**
     * Sets up comprehensive logging configuration.
     */
    private void setupLogging() {
        rootLogger = Logger.getLogger(LOGGER_NAME);
        rootLogger.setLevel(Level.ALL);

        // Remove default handlers to avoid duplicate logs
        rootLogger.setUseParentHandlers(false);

        try {
            // Console handler for immediate feedback
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            consoleHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(consoleHandler);

            // File handler for persistent logging
            Path logDir = Paths.get("logs");
            logDir.toFile().mkdirs();

            FileHandler fileHandler = new FileHandler(
                    logDir.resolve("library_%g.log").toString(),
                    1024 * 1024, // 1MB per file
                    5,           // Keep 5 files
                    true         // Append mode
            );
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(fileHandler);

            // Error-only file handler
            FileHandler errorHandler = new FileHandler(
                    logDir.resolve("library_errors_%g.log").toString(),
                    512 * 1024,  // 512KB per file
                    3,           // Keep 3 files
                    true
            );
            errorHandler.setLevel(Level.WARNING);
            errorHandler.setFormatter(new CustomFormatter());
            rootLogger.addHandler(errorHandler);

        } catch (IOException e) {
            System.err.println("Failed to setup file logging: " + e.getMessage());
        }
    }

    /**
     * Gets a logger for a specific class.
     */
    public Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setParent(rootLogger);
        return logger;
    }

    /**
     * Custom formatter for better log readability.
     */
    private static class CustomFormatter extends Formatter {
        private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();

            // Timestamp
            sb.append(LocalDateTime.now().format(timeFormatter));
            sb.append(" ");

            // Level with color coding for console
            String level = String.format("%-7s", record.getLevel().getName());
            sb.append(level);
            sb.append(" ");

            // Logger name (shortened)
            String loggerName = record.getLoggerName();
            if (loggerName.contains(".")) {
                String[] parts = loggerName.split("\\.");
                loggerName = parts[parts.length - 1];
            }
            sb.append(String.format("%-20s", loggerName));
            sb.append(" - ");

            // Message
            sb.append(formatMessage(record));

            // Exception if present
            if (record.getThrown() != null) {
                sb.append("\n");
                sb.append(getStackTrace(record.getThrown()));
            }

            sb.append("\n");
            return sb.toString();
        }

        private String getStackTrace(Throwable throwable) {
            StringBuilder sb = new StringBuilder();
            sb.append(throwable.getClass().getSimpleName());
            sb.append(": ");
            sb.append(throwable.getMessage());

            // Only include first few stack trace elements to avoid log spam
            StackTraceElement[] elements = throwable.getStackTrace();
            for (int i = 0; i < Math.min(5, elements.length); i++) {
                sb.append("\n\tat ");
                sb.append(elements[i].toString());
            }

            if (elements.length > 5) {
                sb.append("\n\t... ");
                sb.append(elements.length - 5);
                sb.append(" more");
            }

            return sb.toString();
        }
    }

    /**
     * Logs library-specific events with structured data.
     */
    public void logLibraryEvent(String event, String entityType, String entityId, Map<String, Object> details) {
        Logger logger = getLogger(LoggingService.class);

        StringBuilder message = new StringBuilder();
        message.append("Event: ").append(event);
        message.append(", Type: ").append(entityType);
        message.append(", ID: ").append(entityId);

        if (details != null && !details.isEmpty()) {
            message.append(", Details: ").append(details);
        }

        logger.info(message.toString());
    }

    /**
     * Logs performance metrics.
     */
    public void logPerformance(String operation, long durationMs, Map<String, Object> metrics) {
        Logger logger = getLogger(LoggingService.class);

        StringBuilder message = new StringBuilder();
        message.append("Performance - Operation: ").append(operation);
        message.append(", Duration: ").append(durationMs).append("ms");

        if (metrics != null && !metrics.isEmpty()) {
            message.append(", Metrics: ").append(metrics);
        }

        if (durationMs > 1000) {
            logger.warning(message.toString());
        } else {
            logger.info(message.toString());
        }
    }

    /**
     * Logs security events.
     */
    public void logSecurityEvent(String event, String userId, String action, boolean success) {
        Logger logger = getLogger(LoggingService.class);

        String message = String.format("Security - Event: %s, User: %s, Action: %s, Success: %s",
                event, userId, action, success);

        if (success) {
            logger.info(message);
        } else {
            logger.warning(message);
        }
    }
}