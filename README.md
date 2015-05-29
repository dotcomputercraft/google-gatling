google-gatling
=========================

## Getting Started with Gatling 

[Gatling](http://gatling.io/) is a high performance load testing tool which uses asynchronous event-driven IO. This makes it very efficient for high concurrency scenarios, allowing you to get many thousands of simulated users from a single machine. Simulation scripts are written in Scala with a user friendly DSL that is easily version controlled.

## SUMMARY

We have seen Gatling in action with its compact and elegant DSL where you can understand a load test script by just reading the script, without looking at the documentation first!

It also provides us with very useful test reports where we can see important characteristics of the test results such as requests/sec and response times.

We also used Gatling to point out a sever scalability issue with the REST service that we used in the test.

## Quick Start

Simple showcase of a maven project using the google-gatling

To test it out, simply execute the following command :

```
mvn gatling:execute -Dgatling.simulationClass=GoogleSimulation -Dusers=<no-of-concurrent-users> -Drest_api_url=<rest_api_url-name>
```


### For running google simulation:

```
mvn gatling:execute -Dgatling.simulationClass=GoogleSimulation -Dusers=1 -Drest_api_url=www.google.com
```

### For running supermanorder simulation:

```
mvn gatling:execute -Dgatling.simulationClass=SupoermaOrderScenario -Dusers=1 -Drest_api_url=localhost:4242
```

### Gatling shell command line

```
./gatling.sh -sf ./src/test/scala/ -rf ~/results/ -s GoogleSimulation
```

```
./run_gatling.sh
```

### Jenkins CI job directory

```
${JENKINS_HOME} + '/jobs/' + ${JOB_NAME} + '/builds/' + ${BUILD_NUMBER} + '/results/'
```

### loading the Gatling program onto Jenkins and running it with a bash shell script

```
#!/bin/bash -x
JAVA_OPTS="-Drest_api_url=www.google.com"
export JAVA_OPTS

./gatling.sh -sf ./src/test/scala/ -rf ./results/ -s GoogleSimulation

```
