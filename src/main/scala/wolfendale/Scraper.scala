package wolfendale

import java.net.URI

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class Scraper(httpClient: HttpClient) {

  def scrape(url: String, accumulator: Map[String, List[String]] = Map.empty): Future[Map[String, List[String]]] = {
    httpClient.get(url).flatMap {
      links =>

        val domain = new URI(url).getHost
        val newAccumulator = accumulator + (url -> links)

        val newPages = newAccumulator.values.toList.flatten.view.filterNot {
          link =>
            newAccumulator.keys.toList.contains(link) || new URI(link).getHost != domain
        }.force

        newPages.foldLeft(Future.successful(newAccumulator)) {
          case (a, page) =>
            a.flatMap {
              scrape(page, _)
            }
        }
    }
  }
}
