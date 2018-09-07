package wolfendale.scraper

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import wolfendale.MapHttpClient

class BasicScraperSpec extends FreeSpec with MustMatchers with ScalaFutures {

  "a scraper" - {

    "must return all the links on each page" in {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.com/foo",
        ),
        "https://example.com/foo" -> List.empty
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new BasicScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }

    "must not not loop" in {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.com"
        )
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new BasicScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }

    "must not scraper other domains" in {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.net/foo"
        )
      )

      val httpClient = MapHttpClient(pages)

      val scraper = new BasicScraper(httpClient)

      whenReady(scraper.scrape("https://example.com")) {
        _ mustEqual pages
      }
    }
  }
}
