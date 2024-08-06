package com.srivastavavivekggn.scala.util.placeholder.format

import java.util.Locale

import org.springframework.context.MessageSource

case class TranslationValueFormatter(messageSource: MessageSource) extends PlaceholderValueFormatter {

  override def canFormat(formatType: String): Boolean = TranslationValueFormatter.FORMAT_TYPE.equals(formatType)

  override def format(value: Any, args: List[String], locale: Locale): String = {
    messageSource.getMessage(value.toString, args.toArray, value.toString, locale)
  }
}

object TranslationValueFormatter {
  final val FORMAT_TYPE = "translate"
}
