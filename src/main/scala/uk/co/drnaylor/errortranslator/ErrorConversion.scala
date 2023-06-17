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
