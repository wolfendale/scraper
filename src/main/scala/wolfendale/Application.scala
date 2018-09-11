package wolfendale

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorMaterializer, KillSwitches}
import akka.stream.scaladsl.{FileIO, Keep, Source}
import wolfendale.flows.{Last, Print, Scraper}

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration._

object Application {

  def main(args: Array[String]): Unit = {

    Config.parse(args, Config()).foreach {
      config =>

        implicit val system: ActorSystem = ActorSystem("scraper")
        implicit val materializer: ActorMaterializer = ActorMaterializer()
        implicit val ec: ExecutionContext = system.dispatcher
        implicit val logging: LoggingAdapter = Logging(system, this.getClass)

        val (killswitch, result) = Source.single(config.url.toString)
          .via(Scraper(config.url.getHost, new DefaultHttpClient))
          .viaMat(KillSwitches.single)(Keep.right)
          .takeWithin(config.timeout)
          .log("scraping")
          .via(Last[Map[String, List[String]]])
          .log("returning result")
          .via(Print(config.url.toString, config.printer))
          .log("printing result")
          .toMat(FileIO.toPath(config.out))(Keep.both).run()

        result.onComplete {
          _ =>
            system.terminate()
        }

        // todo for some reason this doesn't print the graph when interrupted
        sys.addShutdownHook {
          killswitch.shutdown()
          Await.result(result, 30.seconds)
        }
    }
  }
}
