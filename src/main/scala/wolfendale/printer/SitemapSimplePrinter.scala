package wolfendale.printer

object SitemapSimplePrinter extends SitemapPrinter {

  override def print(title: String, sitemap: Map[String, List[String]]): String =
    sitemap.keySet.mkString("\n")
}
