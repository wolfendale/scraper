package wolfendale.flows

import akka.NotUsed
import akka.stream.scaladsl.Flow

object Last {

  def apply[A]: Flow[A, A, NotUsed] =
    Flow[A].reduce {
      (_, next) => next
    }
}
