package uk.co.drnaylor.errortranslator.integration

import uk.co.drnaylor.errortranslator.ErrorTranslator

object ErrorTranslators {

  implicit val nonPositiveConverter
      : ErrorTranslator[Errors.NonPositive.type, Errors.PresentationError] =
    (_: Errors.NonPositive.type) => Errors.PresentationError("NonPositive")

  implicit val notEvenConverter
      : ErrorTranslator[Errors.NotEven.type, Errors.PresentationError] =
    (_: Errors.NotEven.type) => Errors.PresentationError("NonEven")

  implicit val tooBigConveter
      : ErrorTranslator[Errors.TooBig.type, Errors.PresentationError] =
    (_: Errors.TooBig.type) => Errors.PresentationError("TooBig")

}
