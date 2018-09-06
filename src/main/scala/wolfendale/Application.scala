package wolfendale

import scala.concurrent.Await
import scala.concurrent.duration._

object Application {

  val scraper = new Scraper(JsoupHttpClient)

  def main(args: Array[String]): Unit = {

    val start = System.currentTimeMillis
    val result = Await.result(scraper.scrape(args(0)), 5.minutes)
    val end = System.currentTimeMillis
    val duration = (end - start) / 1000

    println(result)
    println(s"Took $duration seconds")
  }
}
