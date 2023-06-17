package uk.co.drnaylor.errortranslator

import cats.data.EitherT
import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ErrorConversionTest extends AnyFreeSpec with Matchers with ScalaFutures {

  import ErrorMonad._

  case class IncomingError(int: Int)
  case class OutgoingError(string: String)

  implicit val errorConverter: ErrorTranslator[IncomingError, OutgoingError] =
    (v1: IncomingError) => OutgoingError(v1.int.toString)

  "ErrorTranslator trait" - new ErrorConversion {

    "converting an Option[IncomingError]" - {

      "when the option contains an error" in {
        Option(IncomingError(1)).asError[OutgoingError] mustBe Some(
          OutgoingError("1")
        )
      }

      "when the option doesn't contain an error" in {
        val input: Option[IncomingError] = None
        input.asError[OutgoingError] mustBe None
      }
    }

    "converting an Either[IncomingError, Double]" - {

      "when the either contains a left of an error" in {
        val input: Either[IncomingError, Double] = Left(IncomingError(1))
        input.errorAs[OutgoingError] mustBe Left(OutgoingError("1"))
      }

      "when the either contains a right of a double" in {
        val input: Either[IncomingError, Double] = Right(1.23)
        input.errorAs[OutgoingError] mustBe Right(1.23)
      }
    }

    "converting an EitherT[Future, IncomingError, Double" - {
      "when the either contains a left of an error" in {
        val input: EitherT[Future, IncomingError, Double] =
          EitherT.leftT(IncomingError(1))
        whenReady(input.errorAs[OutgoingError].value) {
          _ mustBe Left(OutgoingError("1"))
        }
      }

      "when the either contains a right" in {
        val input: EitherT[Future, IncomingError, Double] =
          EitherT.rightT(1.23)
        whenReady(input.errorAs[OutgoingError].value) {
          _ mustBe Right(1.23)
        }
      }
    }

  }

}
