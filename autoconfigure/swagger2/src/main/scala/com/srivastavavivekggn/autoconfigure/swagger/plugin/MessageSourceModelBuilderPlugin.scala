package com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin

import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerProperties
import org.springframework.context.MessageSource
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.schema.ModelBuilderPlugin
import springfox.documentation.spi.schema.contexts.ModelContext

/**
  * Swagger model plugin that utilizes Spring's MessageSource
  *
  * @param messageSource     the message source
  * @param swaggerProperties the swagger properties
  */
class MessageSourceModelBuilderPlugin(val messageSource: MessageSource,
                                      val swaggerProperties: SwaggerProperties)
  extends AbstractMessageSourcePlugin with ModelBuilderPlugin {

  /**
    * Apply this plugin in the given model context
    *
    * @param context the model context
    */
  override def apply(context: ModelContext): Unit = {
    val model = context.getBuilder.build()

    val typeName = model.getType.getErasedType

    context.getBuilder
      .name(getModelMessage(typeName, "name", model.getName))
      .description(getModelMessage(typeName, "description", model.getDescription))
  }

  /**
    * Determine if this plugin supports the doc type
    *
    * @param delimiter the doc type
    * @return true if the message source is defined
    */
  override def supports(delimiter: DocumentationType): Boolean = Option(messageSource).isDefined
}
