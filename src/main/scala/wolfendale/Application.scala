package wolfendale

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import wolfendale.scraper.StreamScraper

import scala.concurrent.Await
import scala.concurrent.duration._

object Application {

  implicit val system: ActorSystem = ActorSystem("scraper")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def main(args: Array[String]): Unit = {

    try {

      val url = args(0)

      val timeout =
        Duration(s"${args(1)} ${args(2)}")
          .asInstanceOf[FiniteDuration]

      val scraper = new StreamScraper(new DefaultHttpClient, timeout)

      val start = System.currentTimeMillis
      val sitemap = Await.result(scraper.scrape(url), Duration.Inf)
      val end = System.currentTimeMillis
      val duration = (end - start) / 1000

      println(SitemapPrinter.print(url, sitemap))
      println(s"Took $duration seconds")

    } finally {
      system.terminate()
    }
  }
}
