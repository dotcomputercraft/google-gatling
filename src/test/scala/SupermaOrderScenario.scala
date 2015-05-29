/**
 * Created by john.montoya on 5/28/15.
 */
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import Headers._

object ExportScenario {

  val scn = scenario("ExportScenario") // A scenario is a chain of requests
    .feed(csv("user_credentials.csv"))
    .exec(http("login request step")
      .post("/api/session") // Here's an example of a POST request
      .body(ELFileBody("loginTemplate.txt")).asJSON
      .headers(post_header)
      )
    .exec(http("Export step")
      .get("/api/export.csv?keyword=Fox&offset=0&limit=25&sort=date&direction=asc"))
    .exec(http("logout request step")
      .delete("/api/session")
    )
}