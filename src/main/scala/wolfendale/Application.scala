package wolfendale

import java.net.URI
import scala.collection.JavaConverters._
import org.jsoup.Jsoup

object Application {

  private type Pages = Map[URI, List[URI]]

  def main(args: Array[String]): Unit = {

    val start = System.currentTimeMillis
    val result = scrape(new URI(args(0)))
    val end = System.currentTimeMillis
    val duration = (end - start) / 1000

    println(result)
    println(s"Took $duration seconds")
  }

  private def scrape(page: URI): Pages =
    scrape(Map(page -> urlToLinks(page)), page.getHost)

  private def urlToLinks(url: URI): List[URI] = {
    Jsoup.parse(url.toURL, 20000).select("a").asScala.map {
      node =>
        new URI(node.absUrl("href"))
    }.toList
  }

  private def scrape(pages: Pages, domain: String): Pages = {
    pages.foldLeft(pages) {
      case (existingPages, (_, links)) =>

        val newPages = links.view
          .filterNot(x => pages.keys.toList.contains(x) || x.getHost != domain)
          .map {
            page =>
              page -> urlToLinks(page)
          }.force.toMap

        existingPages ++ newPages
    }
  }
}
