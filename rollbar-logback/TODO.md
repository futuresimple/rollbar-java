# TODO
+ async appender
- must not affect the application if e.g. rollbar is down (timeouts for the client?)
- a way to pass request (something like RollbarFilter from rollbar-logback), not sure if it should use MDC
- a way to sanitizing http headers
- a way to provide other contextual data (person)
- connection reuse (nice to have)
- add comments to main types and methods
- spring boot starter
	- default impl of http filter which adds headers
	- a way to add own contextual information providers
	- another feature would be to support @ControllerAdvice when one is not using logback
	    see: https://github.com/rollbar/rollbar-java/issues/15
- instrumentation
	- metric which shows how many messages to rollbar are queued up (nice to have)
	  or a way to add it easily
- properly fingerprint ("group") stacktraces containing java8 lambdas (there are numbers in method names which change 
    sometimes at runtime)
	maybe rollbar fixed default aggregation and it works out fo the box?
	hard to test
- exceptions which provide custom fingerprinter
	maybe it can be configured via web ui and the functionality dropped? we used it only once for grouping by 
	http error codes
