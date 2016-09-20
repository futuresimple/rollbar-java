package com.rollbar.logback;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.StatusUtil;
import ch.qos.logback.core.util.StatusPrinter;
import com.rollbar.logback.payload.DefaultPayloadBuilder;
import com.rollbar.logback.payload.PayloadBuilder;
import com.rollbar.payload.Payload;
import com.rollbar.sender.PayloadSender;
import com.rollbar.sender.RollbarResponse;
import com.rollbar.sender.Sender;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static ch.qos.logback.core.status.StatusUtil.filterStatusListByTimeThreshold;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

/**
 * Created by mabn on 18/09/16.
 */
public class RollbarAppenderTest {
    private static final Logger log = (Logger) LoggerFactory.getLogger(RollbarAppenderTest.class);


    Sender senderMock;
    PayloadBuilder builderMock;

    @Before
    public void initMocks() {
        senderMock = mock(Sender.class);
        builderMock = mock(PayloadBuilder.class);

        when(builderMock.create(anyString(), anyString(), any(ILoggingEvent.class)))
                .thenReturn(Payload.fromMessage("a", "b", "c", null));
        when(senderMock.send(any(Payload.class)))
                .thenReturn(RollbarResponse.success("uuid"));
    }
    @Before
    @After
    public void resetLogging() {
        getContext().reset();
        getContext().getStatusManager().clear();
    }

    @Test
    public void usesConfiguredEndpoint() {
        configureLogging("root_rollbar.xml");

        RollbarAppenderImpl appender = getAppender();
        log.warn("x");

        assertThat(appender.getEndpoint()).isEqualTo("https://someCustomDomain.com/");
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void usesConfiguredEnvironment() {
        configureLogging("root_rollbar.xml");

        RollbarAppenderImpl appender = getAppender();
        log.warn("x");

        assertThat(appender.getEnvironment()).isEqualTo("testing");
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void usesConfiguredAccessToken() {
        configureLogging("root_rollbar.xml");

        RollbarAppenderImpl appender = getAppender();
        log.warn("x");

        assertThat(appender.getAccessToken()).isEqualTo("e3a49f757f86465097c000cb2de9de08");
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void usesDefaultEndpointWhenNotProvided() {
        configureLogging("root_rollbar_no_endpoint.xml");

        RollbarAppenderImpl appender = getAppender();
        log.warn("x");

        assertThat(appender.getEndpoint()).isEqualTo(PayloadSender.DEFAULT_API_ENDPOINT);
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void requiresAccessToken() {
        configureLogging("root_rollbar_no_token.xml");
        log.warn("x");
        assertErrorStatusTextContains("No accessToken set for the appender named [rollbar-appender-sync]");
    }

    @Test
    public void requiresEnvironment() {
        configureLogging("root_rollbar_no_env.xml");
        log.warn("x");
        assertErrorStatusTextContains("No environment set for the appender named [rollbar-appender-sync]");
    }

    @Test
    public void usesDefaultSenderImplementation() {
        configureLogging("root_rollbar.xml");

        RollbarAppenderImpl appender = getAppender();
        assertThat(appender.getSender()).isInstanceOf(PayloadSender.class);

        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void usesDefaultPayloadBuilder() {
        configureLogging("root_rollbar.xml");

        RollbarAppenderImpl appender = getAppender();
        assertThat(appender.getPayloadBuilder()).isInstanceOf(DefaultPayloadBuilder.class);

        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void testLoggingDebug() throws Exception {
        configureLogging("root_rollbar.xml");
        RollbarAppenderImpl appender = getAppender();
        appender.setSender(senderMock);
        appender.setPayloadBuilder(builderMock);

        //when
        log.debug("something");
        Thread.sleep(100);

        //then
        verifyZeroInteractions(senderMock, builderMock);
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void testLoggingInfo() throws Exception {
        configureLogging("root_rollbar.xml");
        RollbarAppenderImpl appender = getAppender();
        appender.setSender(senderMock);
        appender.setPayloadBuilder(builderMock);

        //when
        log.info("something");
        Thread.sleep(100);

        //then
        verifyZeroInteractions(senderMock, builderMock);
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void testLoggingWarn() throws Exception {
        configureLogging("root_rollbar.xml");
        RollbarAppenderImpl appender = getAppender();
        appender.setSender(senderMock);
        appender.setPayloadBuilder(builderMock);

        //when
        log.warn("something");
        Thread.sleep(100);

        //then
        verify(builderMock).create(eq(appender.getAccessToken()), eq(appender.getEnvironment()), any(ILoggingEvent.class));
        verify(senderMock).send(any(Payload.class));
        verifyNoMoreInteractions(senderMock, builderMock);
        assertNoLoggingWarningsOrErrors();
    }

    @Test
    public void testLoggingError() throws Exception {
        configureLogging("root_rollbar.xml");
        RollbarAppenderImpl appender = getAppender();
        appender.setSender(senderMock);
        appender.setPayloadBuilder(builderMock);

        //when
        log.error("something");
        Thread.sleep(100);

        //then
        verify(builderMock).create(eq(appender.getAccessToken()), eq(appender.getEnvironment()), any(ILoggingEvent.class));
        verify(senderMock).send(any(Payload.class));
        verifyNoMoreInteractions(senderMock, builderMock);
        assertNoLoggingWarningsOrErrors();
    }

    private RollbarAppenderImpl getAppender() {
        RollbarAppender ra = (RollbarAppender) getContext().getLogger(Logger.ROOT_LOGGER_NAME).getAppender("ROLLBAR");
        return ra.getChild();
    }

    private void configureLogging(String configFile) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            configurator.doConfigure(getClass().getResourceAsStream(configFile));
        } catch (JoranException je) {
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    private LoggerContext getContext() {
        return (LoggerContext) LoggerFactory.getILoggerFactory();
    }

    private void assertNoLoggingWarningsOrErrors() {
        StatusUtil statusUtil = new StatusUtil(getContext());
        StatusManager sm = getContext().getStatusManager();
        if (statusUtil.getHighestLevel(0) >= ErrorStatus.WARN) {
            List<Status> issues = filterStatusListByTimeThreshold(sm.getCopyOfStatusList(), 0);
            StatusPrinter.printInCaseOfErrorsOrWarnings(getContext());
            throw new AssertionError("" + issues);
        }
    }

    private void assertErrorStatusTextContains(String text) {
        StatusManager sm = getContext().getStatusManager();
        boolean found = false;
        for (Status status : sm.getCopyOfStatusList()) {
            if (status.getEffectiveLevel() >= Status.ERROR) {
                assertThat(status.getMessage()).contains(text);
                found = true;
            }
        }
        if (!found) {
            fail("Error status not found");
        }
    }
}
