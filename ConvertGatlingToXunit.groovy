import groovy.io.FileType
import groovy.xml.MarkupBuilder

/**
 * Special XUnit converter for transformation of gatling log into the XUnit results format.
 * Suitable for jenkins job to be able to
 */

def thr = Thread.currentThread()
def build = thr?.executable
def props = build.parent.builds[0].properties.get("envVars")

println "props: " + props

def jenkinshome = props.get('JENKINS_HOME')
def workspace = props.get('WORKSPACE')
def jobname = props.get('JOB_NAME')
def buildnumber = props.get('BUILD_NUMBER')

println "WORKSPACE: " + workspace
println "JENKINS_HOME: " + jenkinshome
println "JOB_NAME: " + jobname
println "BUILD_NUMBER: " + buildnumber

//Map<String, String> props = System.getenv();
def currentDir = jenkinshome + '/jobs/' + jobname + '/builds/' + buildnumber
println "currentDir : " + currentDir

def gatlingSimulationLog = findGatlingSimulationLog(currentDir)

def failures = getErrorsFromSimulationLog(gatlingSimulationLog)
int okCount = getNumberOfSuccessfulRequests(gatlingSimulationLog)

def xuniReportFile = (gatlingSimulationLog.absolutePath - ".log") + ".xml"
generateXunitReport(okCount, failures, xuniReportFile)

//--------------------------------------------------- HELPER METHODS ---------------------------------------------------
private File findGatlingSimulationLog(pathName) {
    // gatling.log is explicitly created by jenkins job by running command similar to following:
    // mvn -f pom.xml clean test | tee target/gatling/gatling.log
    def filePath = pathName + "/gatling.log"

    println "GatlingSimulationLog - " + filePath

    new File(filePath)
}

/**
 * Gather all errors from simulation log.
 * Each request that failed has own record in simulation.log with message "Check 'in' failed".
 * @param gatlingSimulationLog
 * @param failures
 * @return
 */
private def getErrorsFromSimulationLog(File gatlingSimulationLog) {
    failures = []
    gatlingSimulationLog.eachLine {
        line ->
            if (line.contains("failed :")) {
                failures << line
            }
    }
    failures
}

/**
 * Finds total number of successful requests from gatling simulation log.
 * @param gatlingSimulationLog
 * @return
 */
private int getNumberOfSuccessfulRequests(File gatlingSimulationLog) {
    okCount = 0
    gatlingSimulationLog.eachLine {
        line ->
            if (line.contains("OK=")) {
                okCount = ((line =~ /OK=(\d+)/)[0][1]).toInteger()
            }
    }
    okCount
}

/**
 * Convert list of failures into the XUnit format and print the result xml to the {@code outFile}.
 *
 * See <a href="http://stackoverflow.com/questions/4922867/junit-xml-format-specification-that-hudson-supports">
 *     junit-xml-format-specification-that-hudson-supports</a>
 * and <a href="https://svn.jenkins-ci.org/trunk/hudson/dtkit/dtkit-format/dtkit-junit-model/src/main/resources/com/thalesgroup/dtkit/junit/model/xsd/junit-4.xsd">
 *     jenkins junit-4.xsd</a>.
 * @param failures collection of failures
 * @param outFile output file
 */
private void generateXunitReport(int okCount, def failures, String outFile) {
    def xml = new MarkupBuilder(new PrintWriter(outFile))
    xml.testsuite(tests: failures.size(), name: 'gatling-suite') {

        // generate failed test cases
        failures.each {
            def errorMessage = it
            testcase(classname: 'gatling', name: 'REQUEST FAILED') {
                failure(type: 'KO', errorMessage)
            }
        }

        // generate ok test cases
        (1..okCount).each {
            testcase(classname: 'gatling', name: 'REQUESTS OK', '')
        }
    }
}
