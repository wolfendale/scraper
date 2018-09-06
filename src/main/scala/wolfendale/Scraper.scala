package wolfendale

import scala.concurrent.Future

class Scraper(httpClient: HttpClient) {

  def scrape(url: String): Future[Map[String, List[String]]] =
    ???
}
