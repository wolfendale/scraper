package wolfendale

import scala.concurrent.Future

final case class MapHttpClient(pages: Map[String, List[String]]) extends HttpClient {

  override def get(url: String): Future[List[String]] =
    Future.successful(pages.getOrElse(url, List.empty))
}
