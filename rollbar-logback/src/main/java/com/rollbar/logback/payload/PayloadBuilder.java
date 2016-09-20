package com.rollbar.logback.payload;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.rollbar.payload.Payload;

/**
 * Created by mabn on 19/09/16.
 */
public interface PayloadBuilder {
    Payload create(String accessToken, String environment, ILoggingEvent event);
}
