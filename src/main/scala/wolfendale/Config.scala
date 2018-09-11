package wolfendale

import java.net.URI
import java.nio.file.{Path, Paths}

import scopt.Read
import wolfendale.printer.{SitemapDotPrinter, SitemapPrinter}

import scala.concurrent.duration._
import scala.util.Try

final case class Config(
                         url: URI = new URI("http://www.example.com"),
                         out: Path = Paths.get("./sitemap"),
                         printer: SitemapPrinter = SitemapDotPrinter,
                         timeout: FiniteDuration = 5.seconds)

object Config extends scopt.OptionParser[Config]("scraper") {

  private implicit val durationRead: Read[FiniteDuration] =
    Read.reads {
      string =>
        Try(Duration(string)).collect { case d: FiniteDuration => d }
          .get
    }

  private implicit val pathRead: Read[Path] =
    Read.reads(Paths.get(_))

  arg[URI]("<url to scrape>").action {
    (url, config) =>
      config.copy(url = url)
  }

  arg[Path]("<output file>").action {
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
  }.text("the maximum time which the scraper should run for (default: 5s)")

  note("")
  note("  printer options are:")
  note("    `dot`    for dot graph language output")
  note("    `simple` for a line-by-line output of scraped urls")
}
