package wolfendale.flow

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.util.ByteString
import org.scalatest.{FreeSpec, MustMatchers}
import org.scalatest.concurrent.ScalaFutures
import wolfendale.flows.Print
import wolfendale.printer.SitemapSimplePrinter
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped

class PrintSpec extends FreeSpec with MustMatchers with ScalaFutures {

  private implicit val system: ActorSystem = ActorSystem("test")
  private implicit val materializer: Materializer = ActorMaterializer()

  "a printer flow" - {

    "must print a sitemap" in assertAllStagesStopped {

      val sitemap = Map(
        "https://example.com/a" -> List(
          "https://example.com/b",
          "https://example.com/c",
          "https://example.org/x"
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

      val expected =
        """https://example.com/e
          |https://example.com/f
          |https://example.com/b
          |https://example.com/g
          |https://example.com/c
          |https://example.com/h
          |https://example.com/d
          |https://example.com/a""".stripMargin

      val source = Source.single(sitemap)
      val sink = Sink.head[ByteString]

      val result = source.via(Print("title", SitemapSimplePrinter)).toMat(sink)(Keep.right).run()

      whenReady(result) {
        _.utf8String mustEqual expected
      }
    }
  }
}
