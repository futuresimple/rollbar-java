Rollbar Logback
============= 

Logback appender for integration with the Rollbar service.

sample logback.xml
------------
```
<appender name="ROLLBAR" class="com.rollbar.logback.RollbarAppender">
    <accessToken>[YOUR API KEY HERE]</accessToken>
    <environment>production</environment>
</appender>

<root level="INFO">
    <appender-ref ref="ROLLBAR"/>
</root>
```

Default behavior
------------
- Log events are buffered i na queue which can hold 256 events. A background threads picks the events one by one, formats then and sends to rollbar.
- When more than 20% of the queue is filled the appender discards events with level WARNING or lower.
- When the queue is full new messages are discarded (neverBlock = true)

Configuration
------------
RollbarAppender requires accessToken and environment. Optionally you can specify rollbar server endpoint. Remaining configuration is the same as for logback AsyncAppender.

In order to filter out warning messages and publish only error you might use the regular logback filtering mechanism:
```
<appender name="ROLLBAR" class="com.rollbar.logback.RollbarAppender">
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
        <level>error</level>
    </filter>
<appender>
```

You can also set all the properties you'd normally set on AsyncAppender: http://logback.qos.ch/manual/appenders.html#AsyncAppender
