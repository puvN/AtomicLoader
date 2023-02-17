# AtomicLoader

## _Description_
Atomic loader is my attempt to create my own loading framework. I worked on a lot 
of projects, and on almost each project we faced an issue how to check if our
application is ready to go prod. On testing environment everything was working perfectly well,
but on production a lot of issues appeared:


- we haven't met our SLA under high RPS
- when our application stopped in production we faced an increasing
waterfall of requests (retries from external services, users pressed F5 lots of times, etc.)
- we could not reproduce such traffic on our testing environment
- our hardware on testing environment differed from production
- and lots of other things..

Atomic Loader is here to help to solve these problems. It's not ideal and was made by
one person - me.

## _High-level architecture_

I follow MVP principle, here is the first iteration:

- it's a simple spring boot project which starts, perform some load on specific target, and finishes
- all testing must be performed on local machine
- there is a config which is divided on two groups: load config and target config. Load config contains params
for load testing, in target config you describe your target application which you are going to test
- there is a config validator to make sure that you have not specified wrong parameters
- there are two modes to perform load testing which is handled by `process-per-instance` feature flag. When it's false
all requests will be sent from single jvm, otherwise several jvms will be started (the number of jvms will be equal the
number of targets)
- Each created jvm (or single one) will send requests to specified REST endpoints. The number of requests and testing 
period is configurable.
- Target application is isolated in separate docker container. This is lightweigth container with target application and
monitoring tools (like agents, os monitoring tools, graphana, etc). This is the reason why application called
`Atomic Loader`.