package wolfendale.scraper

import akka.actor.ActorSystem
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped
import akka.stream.{ActorMaterializer, Materializer}
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{FreeSpec, MustMatchers}
import wolfendale.MapHttpClient

class StreamScraperSpec extends FreeSpec with MustMatchers with ScalaFutures with IntegrationPatience {

  implicit val system: ActorSystem = ActorSystem("test")
  implicit val materializer: Materializer = ActorMaterializer()

  "a scraper" - {

    "must return all the links on each page" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.com/foo",
        ),
        "https://example.com/foo" -> List.empty
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new StreamScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }

    "must not not loop" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.com"
        )
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new StreamScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }

    "must not scrape other domains" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.net/foo"
        )
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new StreamScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }

    "must scrape all pages" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com/a" -> List(
          "https://example.com/b",
          "https://example.com/c"
        ),
        "https://example.com/b" -> List.empty,
        "https://example.com/c" -> List(
          "https://example.com/d"
        ),
        "https://example.com/d" -> List(
          "https://example.com/e",
          "https://example.com/f",
          "https://example.com/g",
          "https://example.com/h"
        ),
        "https://example.com/e" -> List.empty,
        "https://example.com/f" -> List.empty,
        "https://example.com/g" -> List.empty,
        "https://example.com/h" -> List.empty
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new StreamScraper(httpClient)

      whenReady(scraper.scrape("https://example.com/a")) {
        _ mustEqual pages
      }
    }
  }
}
