package wolfendale

import org.jsoup.Jsoup

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._

object JsoupHttpClient extends HttpClient {

  override def get(url: String): Future[List[String]] =
    Future {
      println(url)
      Jsoup.connect(url).get().select("a").asScala.toList.map {
        _.absUrl("href")
      }
    }
}
