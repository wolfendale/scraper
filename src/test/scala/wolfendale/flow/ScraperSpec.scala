package wolfendale.flow

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped
import akka.stream.{ActorMaterializer, Materializer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import wolfendale.MapHttpClient
import wolfendale.flows.Scraper

class ScraperSpec extends FreeSpec with MustMatchers with ScalaFutures {

  private implicit val system: ActorSystem = ActorSystem("test")
  private implicit val materializer: Materializer = ActorMaterializer()

  "a scraper flow" - {

    "must scrape a page with no links" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com" -> List.empty
      )

      val httpClient = MapHttpClient(pages)

      val source = Source.single("https://example.com")
      val sink = Sink.last[Map[String, List[String]]]

      val result = source
        .via(Scraper("example.com", httpClient))
        .toMat(sink)(Keep.right).run()

      whenReady(result) {
        _ mustEqual pages
      }
    }

    "must return all the links on each page" in assertAllStagesStopped {

      val pages = Map(
        "https://example.com" -> List(
          "https://example.com/foo",
        ),
        "https://example.com/foo" -> List.empty
      )

      val httpClient = MapHttpClient(pages)

      val source = Source.single("https://example.com")
      val sink = Sink.last[Map[String, List[String]]]

      val result = source
        .via(Scraper("example.com", httpClient))
        .toMat(sink)(Keep.right).run()

      whenReady(result) {
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

      val source = Source.single("https://example.com")
      val sink = Sink.last[Map[String, List[String]]]

      val result = source
        .via(Scraper("example.com", httpClient))
        .toMat(sink)(Keep.right).run()

      whenReady(result) {
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

      val source = Source.single("https://example.com")
      val sink = Sink.last[Map[String, List[String]]]

      val result = source
        .via(Scraper("example.com", httpClient))
        .toMat(sink)(Keep.right).run()

      whenReady(result) {
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

      val source = Source.single("https://example.com/a")
      val sink = Sink.last[Map[String, List[String]]]

      val result = source
        .via(Scraper("example.com", httpClient))
        .toMat(sink)(Keep.right).run()

      whenReady(result) {
        _ mustEqual pages
      }
    }
  }
}
