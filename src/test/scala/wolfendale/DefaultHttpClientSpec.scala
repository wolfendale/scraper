package wolfendale

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FreeSpec, MustMatchers}

import scala.io.Source

class DefaultHttpClientSpec extends FreeSpec with MustMatchers with ScalaFutures
  with IntegrationPatience with BeforeAndAfterAll with BeforeAndAfterEach {

  implicit val system: ActorSystem = ActorSystem("test")
  implicit val materializer: Materializer = ActorMaterializer()

  private val client = new DefaultHttpClient

  private lazy val server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

  "default http client" - {

    "must load a file and return the contents of the `href` attribute from all the <a> elements in the document" in {

      val html = Source.fromResource("foo.html").mkString

      server.stubFor(
        get(urlEqualTo("/foo.html"))
          .willReturn(aResponse().withBody(html))
      )

      val result = client.get(server.url("foo.html"))

      whenReady(result) {
        _ must contain only server.url("foo.html")
      }
    }

    "must return an empty list of links for non-html content types" in {

      server.stubFor(
        get(urlEqualTo("/bar"))
          .willReturn(aResponse()
            .withHeader("Content-Type", "application/pdf"))
      )

      val result = client.get(server.url("bar"))

      whenReady(result) {
        _ mustBe empty
      }
    }

    "must follow redirects" in {

      val html = Source.fromResource("foo.html").mkString

      server.stubFor(
        get(urlEqualTo("/foo"))
          .willReturn(aResponse()
            .withStatus(301)
            .withHeader("Location", server.url("bar")))
      )

      server.stubFor(
        get(urlEqualTo("/bar"))
          .willReturn(aResponse().withBody(html))
      )

      val result = client.get(server.url("foo"))

      whenReady(result) {
        _ must contain only server.url("foo.html")
      }
    }

    "must recover a failed future" in {

      server.stubFor(
        get(urlEqualTo("/foo"))
          .willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER))
      )

      val result = client.get(server.url("foo"))

      whenReady(result) {
        _ mustBe empty
      }
    }
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    server.start()
  }

  override def afterAll(): Unit = {
    server.stop()
    super.afterAll()
  }

  override def beforeEach(): Unit = {
    super.beforeEach()
    server.resetAll()
  }
}
