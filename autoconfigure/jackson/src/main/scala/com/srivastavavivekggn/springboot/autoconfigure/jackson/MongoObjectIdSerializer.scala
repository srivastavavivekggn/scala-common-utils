package com.srivastavavivekggn.springboot.autoconfigure.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import org.bson.types.ObjectId


class MongoObjectIdSerializer extends JsonSerializer[ObjectId] {
  override def serialize(value: ObjectId,
                         gen: JsonGenerator,
                         serializers: SerializerProvider): Unit = {

    gen.writeStartObject()
    gen.writeNumberField("timestamp", value.getTimestamp)
    gen.writeObjectField("date", value.getDate)
    gen.writeStringField("hex", value.toHexString)
    gen.writeEndObject()

  }
}
