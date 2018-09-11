package wolfendale

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import wolfendale.printer.SitemapDotPrinter
import wolfendale.scraper.StreamScraper

import scala.concurrent.Await
import scala.concurrent.duration._

object Application {


  def main(args: Array[String]): Unit = {

    Config.parse(args, Config()).foreach {
      config =>

        implicit val system: ActorSystem = ActorSystem("scraper")
        implicit val materializer: ActorMaterializer = ActorMaterializer()

        try {

          val url = args(0)

          val scraper = new StreamScraper(new DefaultHttpClient, config.timeout)
          val sitemap = Await.result(scraper.scrape(url), Duration.Inf)

          println(SitemapDotPrinter.print(url, sitemap))

        } finally {
          system.terminate()
        }
    }
  }
}
