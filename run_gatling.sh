#!/bin/bash -x
JAVA_OPTS="-Drest_api_url=www.google.com"
export JAVA_OPTS

export REPORTS_DIR=${WORKSPACE}'/results/'

/usr/local/gatling-charts-highcharts-bundle-2.1.6/bin/gatling.sh -sf ./src/test/scala/ -rf ./reports -s GoogleSimulation
