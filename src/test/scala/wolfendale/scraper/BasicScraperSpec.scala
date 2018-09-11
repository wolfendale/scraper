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

    "must scrape all pages" in {

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

      val scraper = new BasicScraper(httpClient)

      whenReady(scraper.scrape("https://example.com/a")) {
        _ mustEqual pages
      }
    }
  }
}
