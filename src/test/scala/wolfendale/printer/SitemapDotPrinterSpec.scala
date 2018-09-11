package wolfendale.printer

import org.scalatest.{FreeSpec, MustMatchers}

class SitemapDotPrinterSpec extends FreeSpec with MustMatchers {

  "dot printer" - {

    "must print a sitemap as a .dot file directed graph" in {

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

      val expected =
        """digraph title {
          |	"https://example.com/d" -> "https://example.com/h"
          |	"https://example.com/c" -> "https://example.com/d"
          |	"https://example.com/d" -> "https://example.com/f"
          |	"https://example.com/a" -> "https://example.com/b"
          |	"https://example.com/d" -> "https://example.com/g"
          |	"https://example.com/a" -> "https://example.com/c"
          |	"https://example.com/d" -> "https://example.com/e"
          |	"https://example.com/a" -> "https://example.org/x"
          |}""".stripMargin

      val result = SitemapDotPrinter.print("title", sitemap)

      SitemapDotPrinter.print("title", sitemap) mustEqual expected
    }
  }
}
