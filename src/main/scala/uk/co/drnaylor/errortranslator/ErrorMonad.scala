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