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
