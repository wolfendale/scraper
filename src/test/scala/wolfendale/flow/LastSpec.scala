package wolfendale.flow

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, Materializer}
import akka.stream.scaladsl.{Keep, Sink, Source}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FreeSpec, MustMatchers}
import wolfendale.flows.Last
import akka.stream.testkit.scaladsl.StreamTestKit.assertAllStagesStopped

class LastSpec extends FreeSpec with MustMatchers with ScalaFutures {

  private implicit val system: ActorSystem = ActorSystem("test")
  private implicit val materializer: Materializer = ActorMaterializer()

  "a last flow" - {

    "must return the last element in a stream" in assertAllStagesStopped {

      val source = Source(List(1, 2, 3))
      val sink = Sink.head[Int]

      val result = source.via(Last[Int]).toMat(sink)(Keep.right).run()

      whenReady(result) {
        _ mustBe 3
      }
    }
  }
}
