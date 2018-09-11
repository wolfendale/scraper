package wolfendale.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow
import akka.util.ByteString
import wolfendale.printer.SitemapPrinter

object Print {

  def apply(title: String, printer: SitemapPrinter): Flow[Map[String, List[String]], ByteString, NotUsed] =
    Flow[Map[String, List[String]]].map {
      printer.print(title, _)
    }
}
