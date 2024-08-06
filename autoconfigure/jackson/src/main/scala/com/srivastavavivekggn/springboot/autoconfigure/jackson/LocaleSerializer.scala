package com.srivastavavivekggn.springboot.autoconfigure.jackson

import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{DeserializationContext, JsonDeserializer, JsonSerializer, SerializerProvider}
import com.srivastavavivekggn.scala.util.lang.LocaleUtils

import java.util.Locale

class LocaleSerializer extends JsonSerializer[Locale] {
  override def serialize(value: Locale, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
    gen.writeString(value.toLanguageTag)
  }
}

class LocaleDeserializer extends JsonDeserializer[Locale] {
  override def deserialize(p: JsonParser, ctxt: DeserializationContext): Locale = LocaleUtils.parseLocale(p.getText)
}
