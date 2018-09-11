package wolfendale

import java.io.File
import java.net.URI

import scopt.Read
import wolfendale.printer.{SitemapDotPrinter, SitemapPrinter}

import scala.concurrent.duration._
import scala.util.Try

final case class Config(
                         url: URI = new URI("http://www.example.com"),
                         out: File = new File("./sitemap"),
                         printer: SitemapPrinter = SitemapDotPrinter,
                         timeout: FiniteDuration = 30.seconds)

object Config extends scopt.OptionParser[Config]("scraper") {

  private implicit val durationRead: Read[FiniteDuration] =
    Read.reads {
      string =>
        Try(Duration(string)).collect { case d: FiniteDuration => d }
          .get
    }

  arg[URI]("<url to scrape>").action {
    (url, config) =>
      config.copy(url = url)
  }

  arg[File]("<output file>").action {
    (file, config) =>
      config.copy(out = file)
  }

  opt[SitemapPrinter]('p', "printer").action {
    (printer, config) =>
      config.copy(printer = printer)
  }.text("the printer which should be used to render to the output file (default: dot)")

  opt[FiniteDuration]('t', "timeout").action {
    (timeout, config) =>
      config.copy(timeout = timeout)
  }.text("the maximum time which the scraper should run for (default: 30s)")

  note("")
  note("  printer options are:")
  note("    `dot`    for dot graph language output")
  note("    `simple` for a line-by-line output of scraped urls")
}
