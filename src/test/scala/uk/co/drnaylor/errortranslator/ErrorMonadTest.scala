/*
 * Copyright 2023 Daniel Naylor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.drnaylor.errortranslator

import cats.data.EitherT
import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ErrorMonadTest
    extends AnyFreeSpec
    with Matchers
    with GivenWhenThen
    with ScalaFutures {

  case class IncomingError(int: Int)
  case class OutgoingError(string: String)

  implicit val errorConverter: ErrorTranslator[IncomingError, OutgoingError] =
    (v1: IncomingError) => OutgoingError(v1.int.toString)

  "ErrorMonads" - {
    "For an Option (Some)" in {
      val incoming = IncomingError(1)
      val incomingMaybe: Option[IncomingError] = Some(incoming)
      val outgoingMaybe: Option[OutgoingError] =
        ErrorMonad.option.errorMap(incomingMaybe, errorConverter)
      outgoingMaybe mustBe Some(OutgoingError("1"))
    }

    "For an Option (None)" in {
      val incomingMaybe: Option[IncomingError] = None
      val outgoingMaybe: Option[OutgoingError] =
        ErrorMonad.option.errorMap(incomingMaybe, errorConverter)
      outgoingMaybe mustBe None
    }

    "For an Either#Left" in {
      Given("an Either containing a IncomingError")
      val incoming = IncomingError(1)
      val incomingMaybe: Either[IncomingError, Unit] = Left(incoming)

      When("it is translated")
      val outgoingMaybe: Either[OutgoingError, Unit] =
        ErrorMonad.either.errorMap(incomingMaybe, errorConverter)

      Then("it is an OutgoingError with a string of 1")
      outgoingMaybe mustBe Left(OutgoingError("1"))
    }

    "For an Either#Right" in {
      Given("an Either containing a Unit")
      val incomingMaybe: Either[IncomingError, Unit] = Right((): Unit)

      When("it is translated")
      val outgoingMaybe: Either[OutgoingError, Unit] =
        ErrorMonad.either.errorMap(incomingMaybe, errorConverter)

      Then("it is still a Right containing a Unit")
      outgoingMaybe mustBe Right((): Unit)
    }

    "For an EitherT#Left" in {
      Given("an EitherT containing a IncomingError")
      val incoming = IncomingError(1)
      val incomingMaybe: EitherT[Future, IncomingError, Unit] =
        EitherT.leftT(incoming)

      When("it is translated")
      val outgoingMaybe: EitherT[Future, OutgoingError, Unit] =
        ErrorMonad
          .eitherTFuture[Unit]
          .errorMap(incomingMaybe, errorConverter)

      Then("it is an OutgoingError with a string of 1")
      whenReady(outgoingMaybe.value) {
        _ mustBe Left(OutgoingError("1"))
      }
    }

    "For an EitherT#Right" in {
      Given("an Either containing a Unit")
      val incomingMaybe: EitherT[Future, IncomingError, Unit] =
        EitherT.rightT((): Unit)

      When("it is translated")
      val outgoingMaybe: EitherT[Future, OutgoingError, Unit] = ErrorMonad
        .eitherTFuture[Unit]
        .errorMap(incomingMaybe, errorConverter)

      Then("it is still a Right containing a Unit")
      whenReady(outgoingMaybe.value) {
        _ mustBe Right((): Unit)
      }
    }

  }

}
