/**
 * Created by john.montoya on 2/3/15.
 */
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Headers._

object GoogleScenario {

  val scn = scenario("Google Scenario") // A scenario is a chain of requests
            .exec(http("Google search scenario")
              .get("/search") // Here's an example of a GET request
              .queryParam("q", "gatling tool")
              )
}
