package wolfendale.printer

import akka.util.ByteString

object SitemapSimplePrinter extends SitemapPrinter {

  override def print(notUsed: String, sitemap: Map[String, List[String]]): ByteString =
    ByteString(sitemap.keySet.mkString("\n"))
}
