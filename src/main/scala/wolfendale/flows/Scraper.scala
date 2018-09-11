package wolfendale.flows

import java.net.URI

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.scaladsl.{Broadcast, Concat, Flow, GraphDSL, Zip}
import akka.stream.{Attributes, FlowShape}
import wolfendale.HttpClient

object Scraper {

  def apply(domain: String, httpClient: HttpClient)(implicit system: ActorSystem): Flow[String, Map[String, List[String]], NotUsed] =
    Flow.fromGraph(GraphDSL.create() {
      implicit builder =>

        import GraphDSL.Implicits._

        implicit val logging: LoggingAdapter = Logging(system, this.getClass)

        val bcast1 = builder.add(Broadcast[String](2))
        val bcast2 = builder.add(Broadcast[Map[String, List[String]]](2))
        val zipper = builder.add(Zip[String, List[String]])
        val concat = builder.add(Concat[String](2))

        val scraper = Flow[String]
          .log("scraping")
          .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
          .mapAsyncUnordered(4)(httpClient.get)

        val scan = Flow[(String, List[String])]
          .scan(Map.empty[String, List[String]])(_ + _).drop(1)
          .log("result")

        val newLinks = Flow[Map[String, List[String]]]
          .map {
            sitemap =>

              val existingPages = sitemap.keySet
              val links = sitemap.values.flatten.toSet

              val newPages = (links -- existingPages)
                .filter(link => new URI(link).getHost == domain)

              newPages
          }
          .takeWhile(_.nonEmpty)
          .mapConcat(_.headOption.toList)

        concat.out ~> bcast1.in
        bcast1.out(0) ~> zipper.in0
        bcast1.out(1) ~> scraper ~> zipper.in1
        zipper.out ~> scan ~> bcast2.in
        bcast2.out(1) ~> newLinks ~> concat.in(1)

        FlowShape(concat.in(0), bcast2.out(0))
    })
}
