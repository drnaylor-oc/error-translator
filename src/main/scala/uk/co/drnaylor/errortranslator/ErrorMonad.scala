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
import cats.syntax.either._

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

trait ErrorMonad[T[_]] {

  def errorMap[I, E](monad: T[I], mapper: I => E): T[E]

}

object ErrorMonad {

  implicit val option: ErrorMonad[Option] = new ErrorMonad[Option] {
    override def errorMap[I, E](monad: Option[I], mapper: I => E): Option[E] = monad.map(mapper)
  }

  implicit def either[R]: ErrorMonad[Either[*, R]] = new ErrorMonad[Either[*, R]] {
    override def errorMap[I, E](monad: Either[I, R], mapper: I => E): Either[E, R] = monad.leftMap(mapper)
  }

  implicit def eitherTFuture[R](implicit ec: ExecutionContext): ErrorMonad[EitherT[Future, *, R]] = new ErrorMonad[EitherT[Future, *, R]] {
    override def errorMap[I, E](monad: EitherT[Future, I, R], mapper: I => E): EitherT[Future, E, R] = monad.leftMap(mapper)
  }

}