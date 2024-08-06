package com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin

import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import org.springframework.context.MessageSource
import springfox.documentation.service.{Parameter, ParameterType}
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.OperationBuilderPlugin
import springfox.documentation.spi.service.contexts.OperationContext

import java.util.Locale

class MessageSourceOperationBuilderPlugin(messageSource: MessageSource) extends OperationBuilderPlugin {

  private def msg(root: String, key: String, default: String, rollup: Boolean = false): String = {

    if (rollup) {
      val splitPfx = (s"api.$root").split("\\.")

      // rebuild prefixes up from the bottom
      // a, then a.b, then a.b.c, etc.
      val prefixes = splitPfx.zipWithIndex.map {
        case (p, i) if i > 0 => s"${splitPfx.take(i).mkString(".")}.$p"
        case (p, _) => p
      }

      // start from the right and work our way down (a.b.c, then a.b, then a)
      prefixes
        .foldRight(None.asInstanceOf[Option[String]])((pfx, value) => {

          // if we have a value, use it, otherwise try the next one down
          value.orElse {
            Option(
              messageSource.getMessage(s"$pfx.$key", Array.empty, null, Locale.getDefault)
            )
          }
        })
        // if we didn't get any message value at this point, just convert camelCase to spaces
        .getOrElse(default)
    }
    else {
      messageSource.getMessage(s"api.$root.$key", Array.empty, default, Locale.getDefault)
    }
  }

  override def apply(context: OperationContext): Unit = {

    val operation = context.operationBuilder().build()
    val rootKey = s"${operation.getSummary}"

    context.operationBuilder()
      .summary(msg(rootKey, "summary", operation.getSummary))
      .notes(msg(rootKey, "notes", operation.getNotes))
      .tags(
        CollectionUtils.asJavaSetOrEmpty(
          CollectionUtils.asScalaSetOrEmpty(operation.getTags).map(t => msg("tags", t, t))
        )
      )
      .parameters(
        CollectionUtils.asJavaListOrEmpty(
          Option(operation.getParameters)
            .map(CollectionUtils.asScalaListOrEmpty)
            .getOrElse(List.empty)
            .map(param => {
              new Parameter(
                param.getName,
                msg(rootKey, s"${param.getParamType}.${param.getName}", param.getDescription, rollup = true),
                param.getDefaultValue,
                param.isRequired,
                param.isAllowMultiple,
                param.isAllowEmptyValue,
                param.getModelRef,
                param.getType.orElse(null),
                param.getAllowableValues,
                ParameterType.from(param.getParamType),
                param.getParamAccess,
                param.isHidden,
                param.getPattern,
                param.getCollectionFormat,
                param.getOrder,
                param.getScalarExample,
                param.getExamples,
                param.getVendorExtentions,
                param.getStyle,
                param.getExplode,
                param.getAllowReserved
              )
            })
        )
      )
  }

  override def supports(delimiter: DocumentationType): Boolean = true
}
