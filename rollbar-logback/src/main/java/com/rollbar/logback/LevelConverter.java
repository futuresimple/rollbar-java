package com.rollbar.logback;

import ch.qos.logback.classic.Level;

/**
 * Created by mabn on 17/09/16.
 */
public class LevelConverter {
    public static com.rollbar.payload.data.Level convert(Level logbackLevel) {
        if (logbackLevel == null) {
            return null;
        } else if (logbackLevel.levelInt > Level.ERROR_INT) {
            return com.rollbar.payload.data.Level.CRITICAL;
        } else if (logbackLevel.isGreaterOrEqual(Level.ERROR)) {
            return com.rollbar.payload.data.Level.ERROR;
        } else if (logbackLevel.isGreaterOrEqual(Level.WARN)) {
            return com.rollbar.payload.data.Level.WARNING;
        } else if (logbackLevel.isGreaterOrEqual(Level.INFO)) {
            return com.rollbar.payload.data.Level.INFO;
        } else {
            return com.rollbar.payload.data.Level.DEBUG;
        }
    }
}
