package wolfendale.printer

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.io.dot._
import implicits._
import scalax.collection.GraphEdge.DiEdge

object SitemapDotPrinter extends SitemapPrinter {

  private def edgeTransformer(dotRoot: DotRootGraph)(inner: Graph[String, DiEdge]#EdgeT): Option[(DotGraph, DotEdgeStmt)] =
    inner.edge match {
      case DiEdge(s, t) =>
        Some((dotRoot, DotEdgeStmt(s.toString, t.toString)))
    }

  override def print(title: String, sitemap: Map[String, List[String]]): String = {

    val dotGraph = DotRootGraph(
      directed = true,
      id = Some(title)
    )

    val graph = sitemap.foldLeft(Graph.empty[String, DiEdge]) {
      case (g, (link, links)) =>
        g ++ links.filter(_.nonEmpty).map(link ~> _)
    }

    graph.toDot(dotGraph, edgeTransformer(dotGraph))
  }
}
