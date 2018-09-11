package wolfendale.printer

import org.scalatest.{FreeSpec, MustMatchers}

class SitemapSimplePrinterSpec extends FreeSpec with MustMatchers {

  "simple printer" - {

    "must print a sitemap as a list of pages that have been scraped" in {

      val sitemap = Map(
        "https://example.com/a" -> List(
          "https://example.com/b",
          "https://example.com/c",
          "https://example.org/x"
        ),
        "https://example.com/b" -> List.empty,
        "https://example.com/c" -> List(
          "https://example.com/d"
        ),
        "https://example.com/d" -> List(
          "https://example.com/e",
          "https://example.com/f",
          "https://example.com/g",
          "https://example.com/h"
        ),
        "https://example.com/e" -> List.empty,
        "https://example.com/f" -> List.empty,
        "https://example.com/g" -> List.empty,
        "https://example.com/h" -> List.empty
      )

      val result = SitemapSimplePrinter.print("title", sitemap)

      val expected =
        """https://example.com/e
          |https://example.com/f
          |https://example.com/b
          |https://example.com/g
          |https://example.com/c
          |https://example.com/h
          |https://example.com/d
          |https://example.com/a""".stripMargin

      result mustEqual expected
    }
  }
}
