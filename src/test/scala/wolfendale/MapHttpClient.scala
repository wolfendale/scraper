package wolfendale

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

final case class MapHttpClient(pages: Map[String, List[String]]) extends HttpClient {

  override def get(url: String): Future[List[String]] =
    Future.successful(pages.getOrElse(url, List.empty))
}

final case class DelayedMapHttpClient(delay: Duration, pages: Map[String, List[String]]) extends HttpClient {
  override def get(url: String): Future[List[String]] = Future {
    Thread.sleep(delay.toMillis)
    pages.getOrElse(url, List.empty)
  }
}
