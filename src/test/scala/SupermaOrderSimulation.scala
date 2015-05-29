/**
 * Created by john.montoya on 5/28/15.
 */
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._


class SupoermaOrderSimulation extends Simulation {

  val rest_api = "http://" + System.getProperty("rest_api_url", "localhost:4242")
  println("URL host is: " + rest_api)

  val noOfUsers: Int = Integer.getInteger("users", 1).intValue
  println("noOfUsers = " + noOfUsers)

  val rampup = Integer.getInteger("rampup", 10).intValue
  println("rampup: " + rampup)

  val httpConf = http
    .baseURL(rest_api) // Here is the root for all relative URLs
    .acceptHeader("application/json")
    .disableCaching
    .disableResponseChunksDiscarding
    .extraInfoExtractor(ExtraInfo => {

        println("requestUrl: " + ExtraInfo.request.getUrl)
        println("requestHeader: " + ExtraInfo.request.getHeaders())
        println("requestCookies: " + ExtraInfo.request.getCookies())

        println("ExtraInfo.status: " + ExtraInfo.status)
        println("ExtraInfo.response.statusCode: " + ExtraInfo.response.statusCode)

        println("responseBody: " + ExtraInfo.response.body.string)
        println("session: " + ExtraInfo.session)
        println("request: " + ExtraInfo.request)

        List[String](ExtraInfo.request.getUrl)
      }
    )

    setUp(SupermaOrderScenario
          .scn
            .inject(
              nothingFor(4 seconds),            // 1
              atOnceUsers(noOfUsers),           // 2
              rampUsers(rampup) over(5 seconds) // 3
              )
            .protocols(httpConf)
          )
          .assertions(
            global.responseTime.max.lessThan(50),
            global.successfulRequests.percent.greaterThan(95),
            global.failedRequests.percent.is(0)
          )

    // The building blocks for profile injection the way you want are:
    //
    // 1. nothingFor(duration): Pause for a given duration.
    // 2. atOnceUsers(nbUsers): Injects a given number of users at once.
    // 3. rampUsers(nbUsers) over(duration): Injects a given number of users with a linear ramp over a given duration.

    // More comprehensive example located in (http://gatling.io/docs/2.0.0-RC2/general/simulation_setup.html)

    // These assertions will qualify the Jenkins build or task .i.e.  .assertions(....) fluent definition
}
