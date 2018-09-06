package wolfendale

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FreeSpec, MustMatchers}

import scala.io.Source

class JsoupHttpClientSpec extends FreeSpec with MustMatchers with ScalaFutures
  with IntegrationPatience with BeforeAndAfterAll with BeforeAndAfterEach {

  private lazy val server = new WireMockServer(WireMockConfiguration.wireMockConfig().dynamicPort())

  "a jsoup http client" - {

    "must load a file and return the contents of the `href` attribute from all the <a> elements in the document" in {

      val html = Source.fromResource("foo.html").mkString

      server.stubFor(
        get(urlEqualTo("/foo.html"))
          .willReturn(aResponse().withBody(html))
      )

      val result = JsoupHttpClient.get(server.url("foo.html"))

      whenReady(result) {
        _ must contain only server.url("foo.html")
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
