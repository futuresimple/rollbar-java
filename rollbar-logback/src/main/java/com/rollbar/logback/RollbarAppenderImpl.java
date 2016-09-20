package com.rollbar.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.rollbar.logback.payload.DefaultPayloadBuilder;
import com.rollbar.logback.payload.PayloadBuilder;
import com.rollbar.payload.Payload;
import com.rollbar.sender.PayloadSender;
import com.rollbar.sender.RollbarResponse;
import com.rollbar.sender.Sender;

import java.net.MalformedURLException;
import java.util.Objects;

import static java.lang.String.format;

/**
 * Created by mabn on 17/09/16.
 */
public class RollbarAppenderImpl extends UnsynchronizedAppenderBase<ILoggingEvent> {
    private static final Level MIN_LOG_LEVEL = Level.WARN;
    private String endpoint = PayloadSender.DEFAULT_API_ENDPOINT;
    private String accessToken;
    private String environment;
    private Sender sender;
    private PayloadBuilder payloadBuilder;

    @Override
    public void start() {
        boolean error = false;

        if(payloadBuilder == null) {
            payloadBuilder = new DefaultPayloadBuilder();
        }

        if (this.accessToken == null || this.accessToken.isEmpty()) {
            addError(format("No accessToken set for the appender named [%s].", getName()));
            error = true;
        }
        if (this.environment == null || this.environment.isEmpty()) {
            addError(format("No environment set for the appender named [%s].", getName()));
            error = true;
        }

        try {
            sender = new PayloadSender(endpoint);
        } catch (MalformedURLException e) {
            addError(format("Malformed URL: '%s' for the appender named [%s].", endpoint, getName()));
            error = true;
        }

        if (!error) {
            super.start();
        }
    }

    @Override
    protected void append(ILoggingEvent event) {
        if(!event.getLevel().isGreaterOrEqual(MIN_LOG_LEVEL)) {
            return;
        }
        Payload payload = buildPayload(event);
        RollbarResponse response = sender.send(payload);
        if (!response.isSuccessful()) {
            addError(format("Sending to Rollbar failed: %s: %s", response.statusCode(), response.errorMessage()));
        }
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        Objects.requireNonNull(environment, "Environment is required");
        this.environment = environment;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        Objects.requireNonNull(accessToken, "AccessToken is required");
        this.accessToken = accessToken;
    }

    public Sender getSender() {
        return sender;
    }

    public PayloadBuilder getPayloadBuilder() {
        return payloadBuilder;
    }

    public void setPayloadBuilder(PayloadBuilder payloadBuilder) {
        this.payloadBuilder = payloadBuilder;
    }

    /**
     * Change sender even while appender is already running.
     *
     * @param sender
     */
    public void setSender(Sender sender) {
        Objects.requireNonNull(sender, "Sender is required");
        this.sender = sender;
    }


    private Payload buildPayload(ILoggingEvent event) {
        return payloadBuilder.create(accessToken, environment, event);
    }
}
