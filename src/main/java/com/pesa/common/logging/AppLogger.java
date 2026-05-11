package com.pesa.common.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppLogger {
    private final Logger logger;

    private AppLogger(Class<?> source) {
        this.logger = LoggerFactory.getLogger(source);
    }

    public static AppLogger get(Class<?> source) {
        return new AppLogger(source);
    }

    public void info(String message, Object... args) {
        logger.info(message, args);
    }

    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    public void error(String message, Object... args) {
        logger.error(message, args);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
}
