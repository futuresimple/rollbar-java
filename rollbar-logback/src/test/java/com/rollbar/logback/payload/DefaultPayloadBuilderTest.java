package com.rollbar.logback.payload;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.rollbar.payload.Payload;
import com.rollbar.payload.data.Data;
import com.rollbar.payload.data.body.*;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by mabn on 21/09/16.
 */
public class DefaultPayloadBuilderTest {

    private DefaultPayloadBuilder builder = new DefaultPayloadBuilder();

    @Test
    public void testDefaultValues() {
        LoggingEvent event = makeEvent(1474411201000L, Level.INFO, "loggerName", "message {} {}", "arg1", "arg2");

        Payload payload = builder.create("key", "env", event);

        // then
        assertThat(payload.accessToken()).isEqualTo("key");
        Data data = payload.data();
        assertThat(data.environment()).isEqualTo("env");
        assertThat(data.notifier().name()).isEqualTo("com.rollbar.rollbar-java:rollbar-logback");
        assertThat(data.language()).isEqualTo("java");
        assertThat(data.platform()).isEqualTo(System.getProperty("java.version"));

        // default version is not present when running tests from IDE and present when run via maven - not testing this:
        // assertThat(data.notifier().version()).isNull();
        assertThat(data.codeVersion()).isNull();
        assertThat(data.custom()).isNull();
        assertThat(data.fingerprint()).isNull();
        assertThat(data.framework()).isNull();
        assertThat(data.person()).isNull();
        assertThat(data.request()).isNull();
        assertThat(data.server()).isNull();
        assertThat(data.title()).isNull();
        assertThat(data.uuid()).isNull();
    }

    @Test
    public void testMessage() {
        LoggingEvent event = makeEvent(1474411201000L, Level.WARN, "loggerName", "message {} {}", "arg1", "arg2");

        Payload payload = builder.create("key", "env", event);

        // then
        assertThat(payload.accessToken()).isEqualTo("key");
        Data data = payload.data();
        assertThat(data.context()).isEqualTo("loggerName");
        assertThat(data.environment()).isEqualTo("env");
        assertThat(data.level()).isEqualTo(com.rollbar.payload.data.Level.WARNING);
        assertThat(data.timestamp()).isEqualTo("2016-09-21T00:40:01");

        Body body = data.body();
        assertThat(body.contents()).isInstanceOf(Message.class);
        assertThat(body.message().body()).isEqualTo("message arg1 arg2");
        assertThat(body.message().getMembers()).hasSize(1); // only body

    }

    @Test
    public void testExceptionNoCause() {
        Throwable t = new RuntimeException("a reason");
        LoggingEvent event = makeEvent(1474411201000L, Level.ERROR, "loggerName", "message {}", t, "arg1");

        Payload payload = builder.create("key", "env", event);

        // then
        assertThat(payload.accessToken()).isEqualTo("key");
        Data data = payload.data();
        assertThat(data.context()).isEqualTo("loggerName");
        assertThat(data.environment()).isEqualTo("env");
        assertThat(data.level()).isEqualTo(com.rollbar.payload.data.Level.ERROR);
        assertThat(data.timestamp()).isEqualTo("2016-09-21T00:40:01");

        Body body = data.body();
        assertThat(body.contents()).isInstanceOf(Trace.class);
        ExceptionInfo info = body.trace().exception();
        assertThat(info.className()).isEqualTo("RuntimeException");
        assertThat(info.description()).isEqualTo("message arg1");
        assertThat(info.message()).isEqualTo("a reason");
        assertThat(body.trace().frames()).isNotEmpty();
    }

    @Test
    public void testExceptionWithCause() {
        Throwable cause = new IllegalStateException("a cause");
        Throwable t = new RuntimeException("a reason", cause);
        LoggingEvent event = makeEvent(1474411201000L, Level.INFO, "loggerName", "message {}", t, "arg1");

        Payload payload = builder.create("key", "env", event);

        // then
        assertThat(payload.accessToken()).isEqualTo("key");
        Data data = payload.data();
        assertThat(data.context()).isEqualTo("loggerName");
        assertThat(data.environment()).isEqualTo("env");
        assertThat(data.level()).isEqualTo(com.rollbar.payload.data.Level.INFO);
        assertThat(data.timestamp()).isEqualTo("2016-09-21T00:40:01");

        Body body = data.body();
        assertThat(body.contents()).isInstanceOf(TraceChain.class);
        Trace[] traces = body.traceChain().traces();
        assertThat(traces.length).isEqualTo(2);
        assertThat(traces[0].exception().className()).isEqualTo("RuntimeException");
        assertThat(traces[0].exception().description()).isEqualTo("message arg1");
        assertThat(traces[0].exception().message()).isEqualTo("a reason");
        assertThat(traces[0].frames()).isNotEmpty();
        assertThat(traces[1].exception().className()).isEqualTo("IllegalStateException");
        assertThat(traces[1].exception().description()).isNull();
        assertThat(traces[1].exception().message()).isEqualTo("a cause");
        assertThat(traces[1].frames()).isNotEmpty();
    }

    private LoggingEvent makeEvent(long timestamp, Level level, String loggerName, String message, Object... messageArgs) {
        LoggingEvent event = new LoggingEvent();
        event.setLevel(level);
        event.setLoggerName(loggerName);
        event.setMessage(message);
        event.setTimeStamp(timestamp);
        event.setArgumentArray(messageArgs);
        return event;
    }

    private LoggingEvent makeEvent(long timestamp, Level level, String loggerName, String message, Throwable ex, Object... messageArgs) {
        LoggingEvent event = new LoggingEvent();
        event.setLevel(level);
        event.setLoggerName(loggerName);
        event.setMessage(message);
        event.setTimeStamp(timestamp);
        event.setArgumentArray(messageArgs);
        ThrowableProxy throwableProxy = new ThrowableProxy(ex);
        event.setThrowableProxy(throwableProxy);
        return event;
    }
}