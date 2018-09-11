package wolfendale.printer

import scopt.Read

trait SitemapPrinter {
  def print(title: String, sitemap: Map[String, List[String]]): String
}

object SitemapPrinter {

  implicit val reads: Read[SitemapPrinter] = Read.reads {
    case "dot"    => SitemapDotPrinter
    case "simple" => SitemapSimplePrinter
  }
}
