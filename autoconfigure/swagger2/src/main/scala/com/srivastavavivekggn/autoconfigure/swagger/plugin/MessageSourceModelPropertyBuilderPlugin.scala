package com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin

import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerProperties
import org.springframework.context.MessageSource
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelPropertyBuilderPlugin
import springfox.documentation.spi.schema.contexts.ModelPropertyContext

class MessageSourceModelPropertyBuilderPlugin(val messageSource: MessageSource,
                                              val swaggerProperties: SwaggerProperties)

  extends AbstractMessageSourcePlugin with ModelPropertyBuilderPlugin {


  override def apply(context: ModelPropertyContext): Unit = {

    val property = context.getBuilder.build()

    val parentClass = Option(context.getBeanPropertyDefinition.orElse(null))
      .filter(bd => Option(bd.getField).isDefined)
      .map(_.getField.getDeclaringClass)

    val propertyName = property.getName

    context.getBuilder
      .description(getModelPropertyMessage(parentClass, propertyName, "description", property.getDescription))
      .example(getModelPropertyMessage(parentClass, propertyName, "example", property.getExample.toString))
      .pattern(getModelPropertyMessage(parentClass, propertyName, "pattern", property.getPattern))
      .position(
        getIntegerModelProperty(parentClass, propertyName, "position", property.getPosition)
      )
      .required(
        getBooleanModelProperty(parentClass, propertyName, "required", property.isRequired)
      )
      .readOnly(
        getBooleanModelProperty(parentClass, propertyName, "readOnly", property.isReadOnly)
      )
  }

  override def supports(delimiter: DocumentationType): Boolean = true
}
