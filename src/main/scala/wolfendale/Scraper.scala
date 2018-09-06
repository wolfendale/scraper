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

        val newPages = links.view.filterNot {
          link =>
            newAccumulator.keys.toList.contains(link) || new URI(link).getHost != domain
        }

        val newPagesAndLinks = newPages.map(scrape(_, newAccumulator)).force

        Future.sequence(newPagesAndLinks).map {
          _.foldLeft(newAccumulator) { _ ++ _ }
        }
    }
  }
}
