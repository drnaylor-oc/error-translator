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

import scala.language.implicitConversions

object ErrorConversion {

  def convert[T[_], I, E](in: T[I])(implicit
      errorMonad: ErrorMonad[T],
      converter: ErrorTranslator[I, E]
  ): T[E] =
    errorMonad.errorMap(in, converter)

}

trait ErrorConversion {

  implicit class ErrorMonadType[T[_], I](value: T[I]) {

    def asError[E](implicit
        errorMonad: ErrorMonad[T],
        converter: ErrorTranslator[I, E]
    ): T[E] =
      ErrorConversion.convert(value)(errorMonad, converter)

  }

  // This also works with EitherT when partial unification is turned on
  implicit class LeftErrorMonadType[T[_, _], I, R](value: T[I, R]) {
    def errorAs[E](implicit
                   errorMonad: ErrorMonad[T[*, R]],
                   converter: ErrorTranslator[I, E]
    ): T[E, R] =
      errorMonad.errorMap(value, converter)
  }

}
