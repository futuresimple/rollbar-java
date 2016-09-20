package com.rollbar.logback;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;

/**
 * Created by mabn on 17/09/16.
 */
public class RollbarAppender extends AsyncAppender {
    public static final String CHILD_NAME = "rollbar-appender-sync";
    private final RollbarAppenderImpl child;

    public RollbarAppender() {
        setNeverBlock(true);

        child = new RollbarAppenderImpl();
    }

    @Override
    public void start() {
        super.start();
        child.start();
    }

    @Override
    protected boolean isDiscardable(ILoggingEvent event) {
        Level level = event.getLevel();
        return level.toInt() <= Level.WARN_INT;
    }

    public RollbarAppenderImpl getChild() {
        return (RollbarAppenderImpl) getAppender(CHILD_NAME);
    }

    @Override
    public void setContext(Context context) {
        super.setContext(context);
        child.setContext(context);
        child.setName(CHILD_NAME);
        addAppender(child);
    }

    public void setEndpoint(String endpoint) {
        child.setEndpoint(endpoint);
    }

    public void setAccessToken(String accessToken) {
        child.setAccessToken(accessToken);
    }

    public void setEnvironment(String environment) {
        child.setEnvironment(environment);
    }
}
