Error Translator for Scala 2.13
===============================

## Overview

A small Scala library to reduce boilerplate and duplication when translating errors from one form to another.

This small library was borne out of the need to easily translate errors between layers of a system, but do so in a way that
allowed for a clean syntax in our monad driven for-comprehensions. This library makes use of typeclasses to simplify such
conversion and requires minimal setup to use with `Option`, `Either` and `EitherT[Future, _, _]`.

This library has been spun out of another project as other projects might find use for this, or I might find use for 
this on other projects.

## Using this library

You can define your conversions by creating a `ErrorTranslator` and defining an `implicit`, e.g.

```scala
implicit val conversion: ErrorTranslator[Incoming, OutgoingOne] = {
  case Incoming.ErrorOne => OutgoingOne
  case Incoming.ErrorTwo => OutgoingTwo
}
```

Then, mix in the `ErrorTranslator` trait and import the appropriate `ErrorMonad`.

```scala
class Example with ErrorTranslator {

  import uk.co.drnaylor.errortranslator.ErrorMonad.either
  
  object ErrorOne
  object ErrorTwo
  
  implicit val errorOne: ErrorTranslator[ErrorOne, PresenetationError] = ???
  implicit val errorTwo: ErrorTranslator[ErrorTwo, PresenetationError] = ???
  
  def someEither(): Either[ErrorOne, Int]          = ???
  def someEither(a: Int): Either[ErrorTwo, Double] = ???
  
  val result: Either[PresentationError, Double] = 
    for {
      a <- someEither().asError[PresentationError]
      b <- someOtherEither(a).asError[PresentationError]
    } yield b
}
```

## Included `ErrorMonad`s

Three are included in this library:

* `option` for converting `Option[E]`, where `E` is the incoming error type
* `either` for converting `Either[E, _]`, where errors are on the left
* `eitherTFuture` for converting `EitherT[Future, E, _]`, where errors are on the left

If you are using another type to convert, you can define your own `ErrorMonad`.