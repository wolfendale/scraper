package wolfendale.scraper

import java.net.URI

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.GraphEdge.DiEdge
import wolfendale.HttpClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BasicScraper(httpClient: HttpClient) extends Scraper {

  override def scrape(url: String): Future[Map[String, List[String]]] =
    scrape(url, Map.empty)

  private def scrape(url: String, accumulator: Map[String, List[String]]): Future[Map[String, List[String]]] = {
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
