package uk.co.drnaylor.errortranslator.integration

object Errors {

  object NonPositive
  object NotEven
  object TooBig

  case class PresentationError(string: String)

}
