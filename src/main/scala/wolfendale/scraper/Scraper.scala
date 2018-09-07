package wolfendale.scraper

import scala.concurrent.Future

trait Scraper {
  def scrape(url: String): Future[Map[String, List[String]]]
}
