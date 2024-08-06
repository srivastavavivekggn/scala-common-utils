package com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin

import org.springframework.context.MessageSource
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.ParameterBuilderPlugin
import springfox.documentation.spi.service.contexts.ParameterContext

case class MessageSourceParameterBuilderPlugin(messageSource: MessageSource) extends ParameterBuilderPlugin {

  override def apply(parameterContext: ParameterContext): Unit = {

    val operationId = parameterContext.getOperationContext.operationBuilder().build().getSummary

    val param = parameterContext.parameterBuilder().build()
    val paramName = parameterContext.resolvedMethodParameter().defaultName().orElse(null)

    if (param.getParamType.equals("header")) {
      val key = s"api.$operationId.${param.getParamType}.$paramName"

      // TODO: this requires further implementation
    }
  }

  override def supports(delimiter: DocumentationType): Boolean = true
}
