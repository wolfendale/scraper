package wolfendale

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Keep, Source}
import wolfendale.flows.{Last, Print, Scraper}

import scala.concurrent.ExecutionContext

object Application {

  def main(args: Array[String]): Unit = {

    Config.parse(args, Config()).foreach {
      config =>

        implicit val system: ActorSystem = ActorSystem("scraper")
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val ec: ExecutionContext = system.dispatcher
        implicit val logging: LoggingAdapter = Logging(system, this.getClass)

        val result = Source.single(config.url.toString)
          .via(Scraper(config.url.getHost, new DefaultHttpClient))
          .takeWithin(config.timeout)
          .via(Last[Map[String, List[String]]])
          .via(Print(config.url.toString, config.printer))
          .runWith(FileIO.toPath(config.out))

        result.onComplete {
          _ =>
            system.terminate()
        }
    }
  }
}
