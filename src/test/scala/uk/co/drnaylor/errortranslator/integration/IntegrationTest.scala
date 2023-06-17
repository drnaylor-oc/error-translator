package uk.co.drnaylor.errortranslator.integration

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import uk.co.drnaylor.errortranslator.{ErrorConversion, ErrorTranslator}
import uk.co.drnaylor.errortranslator.integration.Errors.PresentationError

import scala.util.Either

class IntegrationTest extends AnyFreeSpec with Matchers {

  "Integration test" - new ErrorConversion {

    import uk.co.drnaylor.errortranslator.ErrorMonad.either
    import ErrorTranslators._

    def isPositive(in: Int): Either[Errors.NonPositive.type, Unit] =
      if (in <= 0) Left(Errors.NonPositive)
      else Right((): Unit)

    def ensureEven(in: Int): Either[Errors.NotEven.type, Unit] =
      if (in % 2 != 0) Left(Errors.NotEven)
      else Right((): Unit)

    def sum(first: Int, second: Int): Either[Errors.TooBig.type, Int] = {
      val result = first + second
      if (result > 9) Left(Errors.TooBig)
      else Right(result)
    }

    def test(first: Int, second: Int): Either[PresentationError, Int] = {
      for {
        _ <- isPositive(first).errorAs[PresentationError]
        _ <- ensureEven(second).errorAs[PresentationError]
        result <- sum(first, second).errorAs[PresentationError]
      } yield result
    }

    "no errors returns the result" in {
      test(1, 2) mustBe Right(3)
    }

    """negative first parameter returns PresentationError("NonPositive")""" in {
      test(-1, 2) mustBe Left(PresentationError("NonPositive"))
    }

    """odd second parameter returns PresentationError("NonEven")""" in {
      test(1, 1) mustBe Left(PresentationError("NonEven"))
    }

    """sum of numbers being greater than 10 returns PresentationError("TooBig")""" in {
      test(8, 4) mustBe Left(PresentationError("TooBig"))
    }
  }

}
