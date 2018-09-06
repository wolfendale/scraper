package wolfendale

import scala.concurrent.Future

trait HttpClient {
  def get(url: String): Future[List[String]]
}
