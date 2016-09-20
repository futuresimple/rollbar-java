package com.rollbar.logback;

import com.rollbar.payload.data.*;
import com.rollbar.payload.data.body.Body;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Created by mabn on 17/09/16.
 */
public final class DataBuilder {
    private String environment;
    private Body body;
    private Level level;
    private Date timestamp;
    private String codeVersion;
    private String platform;
    private String language;
    private String framework;
    private String context;
    private Request request;
    private Person person;
    private Server server;
    private Map<String, Object> custom;
    private String fingerprint;
    private String title;
    private UUID uuid;
    private Notifier notifier;

    public DataBuilder environment(String environment) {
        this.environment = environment;
        return this;
    }

    public DataBuilder body(Body body) {
        this.body = body;
        return this;
    }

    public DataBuilder level(Level level) {
        this.level = level;
        return this;
    }

    public DataBuilder timestamp(Date timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public DataBuilder codeVersion(String codeVersion) {
        this.codeVersion = codeVersion;
        return this;
    }

    public DataBuilder platform(String platform) {
        this.platform = platform;
        return this;
    }

    public DataBuilder language(String language) {
        this.language = language;
        return this;
    }

    public DataBuilder framework(String framework) {
        this.framework = framework;
        return this;
    }

    public DataBuilder context(String context) {
        this.context = context;
        return this;
    }

    public DataBuilder request(Request request) {
        this.request = request;
        return this;
    }

    public DataBuilder person(Person person) {
        this.person = person;
        return this;
    }

    public DataBuilder server(Server server) {
        this.server = server;
        return this;
    }

    public DataBuilder fingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public DataBuilder title(String title) {
        this.title = title;
        return this;
    }

    public DataBuilder uuid(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public DataBuilder notifier(Notifier notifier) {
        this.notifier = notifier;
        return this;
    }

    public void custom(Map<String, Object> custom) {
        this.custom = custom;
    }

    public Data build() {
        return new Data(environment, body, level, timestamp, codeVersion, platform, language, framework, context, request, person, server, custom, fingerprint, title, uuid, notifier);
    }
}
