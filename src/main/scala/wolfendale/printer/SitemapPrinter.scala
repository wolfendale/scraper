package wolfendale.printer

import akka.util.ByteString
import scopt.Read

trait SitemapPrinter {
  def print(title: String, sitemap: Map[String, List[String]]): ByteString
}

object SitemapPrinter {

  implicit val reads: Read[SitemapPrinter] = Read.reads {
    case "dot"    => SitemapDotPrinter
    case "simple" => SitemapSimplePrinter
  }
}
