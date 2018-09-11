package wolfendale

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.sttp._
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import org.jsoup.Jsoup

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}

class DefaultHttpClient(implicit system: ActorSystem, materializer: Materializer) extends HttpClient {

  private implicit val ec: ExecutionContext = system.dispatcher
  private implicit val backend: SttpBackend[Future, Source[ByteString, Any]] =
    AkkaHttpBackend.usingActorSystem(system)

  override def get(url: String): Future[List[String]] = {
    sttp.get(uri"$url").send().map {
      response =>
        Jsoup.parse(response.body.merge, url)
          .select("a").asScala.toList
          .map(_.absUrl("href"))
    }
  }
}
