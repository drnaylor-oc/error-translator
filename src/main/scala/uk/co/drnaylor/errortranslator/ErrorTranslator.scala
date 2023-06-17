package uk.co.drnaylor.errortranslator

trait ErrorTranslator[I, E] extends (I => E)