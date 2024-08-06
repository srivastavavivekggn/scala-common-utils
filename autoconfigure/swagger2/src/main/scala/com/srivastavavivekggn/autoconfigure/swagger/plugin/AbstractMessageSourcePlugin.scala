package com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin

import java.util.Locale

import com.srivastavavivekggn.scala.util.TypeAlias.JBoolean
import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerProperties
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.context.{MessageSource, NoSuchMessageException}

abstract class AbstractMessageSourcePlugin {

  /**
    * simple reusable empty array of type AnyRef
    */
  private lazy val emptyArray = Array.empty[AnyRef]

  /**
    * reusable period value
    */
  private val period = "."

  /**
    * The default locale to use
    *
    * @return the default locale
    */
  def defaultLocale: Locale = Locale.getDefault

  /**
    * The message source to use for this plugin
    *
    * @return the message source
    */
  def messageSource: MessageSource

  /**
    * The configured properties
    *
    * @return the swagger properties
    */
  def swaggerProperties: SwaggerProperties

  /**
    * Get's the model prefix value
    *
    * @return the model prefix with a "." after it
    */
  def modelPrefix: String = Option(swaggerProperties.messageSource.modelPrefix).map(_ + period).getOrElse("")

  /**
    * Get the operation prefix value
    *
    * @return the operation prefix with a "." after it
    */
  def operationPrefix: String = Option(swaggerProperties.messageSource.operationPrefix).map(_ + period).getOrElse("")

  /**
    * Normalize the incoming type name
    *
    * @param typ the type
    * @return returns a lowercased simple type name -> java.util.ArrayList becomes arrayList
    */
  def normalizeTypeName(typ: Class[_]): String = s"${typ.getSimpleName.head.toLower}${typ.getSimpleName.tail}"

  /**
    * Get a model message
    *
    * @param model    the model class
    * @param docField the documentation field
    * @param default  the default value
    * @return the model message value
    */
  def getModelMessage(model: Class[_], docField: String, default: String): String = {

    val className = normalizeTypeName(model)

    getMessage(Array(s"$modelPrefix$className.$docField"), emptyArray, default)
  }

  /**
    * Get a model property
    *
    * @param model    the model class
    * @param property the property
    * @param docField the swagger doc field
    * @param default  the default value
    * @return the value
    */
  def getModelPropertyMessage(model: Option[Class[_]], property: String, docField: String, default: String): String = {

    // we will always try this message key
    val alwaysCodes = Array(s"$modelPrefix$property.$docField")

    val codes = model match {
      // parent model passed in
      case Some(m) => Array(s"$modelPrefix${normalizeTypeName(m)}.$property.$docField") ++ alwaysCodes

      // no parent class, just search the 'always' key
      case None => alwaysCodes
    }

    getMessage(codes, emptyArray, default)
  }

  /**
    * Get an integer value from message source
    *
    * @param model    the model
    * @param property the property
    * @param docField the doc field
    * @param default  the default value
    * @return the parsed value
    */
  def getIntegerModelProperty(model: Option[Class[_]],
                              property: String,
                              docField: String,
                              default: Integer): Integer = {
    StringUtils.nonEmpty(
      getModelPropertyMessage(model, property, docField, None.orNull), trim = true
    ) match {
      case None => default
      case Some(s) => s.toInt
    }
  }

  /**
    * Get a boolean value from message source
    *
    * @param model    the model
    * @param property the property
    * @param docField the doc field
    * @param default  the default value
    * @return the parsed value
    */
  def getBooleanModelProperty(model: Option[Class[_]],
                              property: String,
                              docField: String,
                              default: Boolean): JBoolean = {
    StringUtils.nonEmpty(
      getModelPropertyMessage(model, property, docField, None.orNull), trim = true
    ) match {
      case None => default
      case Some(s) => s.equalsIgnoreCase("true")
    }
  }


  /**
    * Gets a value from the messageSource trying each code in order until it finds one
    *
    * @param codes   the codes to try
    * @param args    the arguments
    * @param default the default message value
    * @param locale  the locale
    * @return the message if found, or the default if no message is found
    */
  protected def getMessage(codes: Array[String],
                           args: Array[AnyRef],
                           default: String,
                           locale: Locale = defaultLocale): String = {

    try {
      messageSource.getMessage(
        new DefaultMessageSourceResolvable(codes, args, default),
        locale
      )
    }
    catch {
      case n: NoSuchMessageException => default
    }
  }

}
