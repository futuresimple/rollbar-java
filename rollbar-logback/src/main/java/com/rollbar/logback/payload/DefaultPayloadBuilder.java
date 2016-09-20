package com.rollbar.logback.payload;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.rollbar.logback.DataBuilder;
import com.rollbar.logback.LevelConverter;
import com.rollbar.payload.Payload;
import com.rollbar.payload.data.Notifier;
import com.rollbar.payload.data.body.Body;

import java.util.Date;

/**
 * Created by mabn on 17/09/16.
 */
public class DefaultPayloadBuilder implements PayloadBuilder {
    private static final Notifier NOTIFIER = new Notifier().name("com.rollbar.rollbar-java:rollbar-logback");
    private static final String PLATFORM = System.getProperty("java.version");

    public Payload create(String accessToken, String environment, ILoggingEvent event) {

        DataBuilder builder = new DataBuilder();
        ch.qos.logback.classic.Level eventLevel = event.getLevel();

        builder.environment(environment);
        builder.level(LevelConverter.convert(event.getLevel()));
        builder.platform(PLATFORM);
        builder.language("java");
        builder.context(event.getLoggerName());
        builder.body(buildBody(event));
//        TODO
//        builder.request();
//        builder.custom();
//        builder.person();
//        builder.server();
        builder.notifier(NOTIFIER);
        builder.timestamp(new Date(event.getTimeStamp()));

        return new Payload(accessToken, builder.build());
    }

    private Body buildBody(ILoggingEvent event) {
        ThrowableProxy throwableProxy = (ThrowableProxy) event.getThrowableProxy();
        if (event.getThrowableProxy() != null) {
            return Body.fromError(((ThrowableProxy) event.getThrowableProxy()).getThrowable(), event.getFormattedMessage());
        } else {
            return Body.fromString(event.getFormattedMessage());
        }
    }
}
