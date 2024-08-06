package com.srivastavavivekggn.springboot.autoconfigure.swagger

import com.srivastavavivekggn.scala.util.TypeAlias.{JArrayList, JHashMap, JList, JMap}
import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerProperties._
import org.springframework.boot.context.properties.ConfigurationProperties

import java.lang.{Boolean => JBoolean}
import scala.beans.BeanProperty
import scala.runtime.BoxedUnit

@ConfigurationProperties(prefix = "swagger")
class SwaggerProperties {

  /**
    * Enabled flag
    */
  @BeanProperty
  var enabled: JBoolean = true

  /**
    * The message source config
    */
  @BeanProperty
  var messageSource: MessageSourceConfig = new MessageSourceConfig

  /**
    * the global api info
    */
  @BeanProperty
  var apiInfo: ApiInfoConfig = new ApiInfoConfig

  /**
    * The contact info
    */
  @BeanProperty
  var contact: ContactConfig = new ContactConfig

  /**
    * Docket configurations
    */
  @BeanProperty
  var dockets: JList[DocketConfig] = new JArrayList[DocketConfig]

  /**
    * The security configuration
    */
  @BeanProperty
  var security: SecurityConfig = new SecurityConfig

  /**
    * The tags
    */
  @BeanProperty
  var tags: JList[SwaggerTag] = new JArrayList[SwaggerTag]()
}

/**
  * Companion object for doc properties
  */
object SwaggerProperties {

  class MessageSourceConfig {

    @BeanProperty
    var modelPrefix: String = null

    @BeanProperty
    var operationPrefix: String = null

  }

  /**
    * Contact config class
    */
  class ContactConfig {

    /**
      * contact name
      */
    @BeanProperty
    var name: String = null

    /**
      * contact url
      */
    @BeanProperty
    var url: String = null

    /**
      * contact email
      */
    @BeanProperty
    var email: String = null
  }

  /**
    * api info class
    */
  class ApiInfoConfig {

    /**
      * api title
      */
    @BeanProperty
    var title: String = null

    /**
      * api description
      */
    @BeanProperty
    var description: String = null

    /**
      * api version
      */
    @BeanProperty
    var version: String = null

    /**
      * api TOS url
      */
    @BeanProperty
    var termsOfServiceUrl: String = null

    /**
      * api license
      */
    @BeanProperty
    var license: String = null

    /**
      * api license url
      */
    @BeanProperty
    var licenseUrl: String = null
  }


  /**
    * the docket config
    */
  class DocketConfig {

    /**
      * the documentation type (this should always be 2)
      */
    @BeanProperty
    var documentationType: Integer = 2

    /**
      * the group name (should be unique across dockets)
      */
    @BeanProperty
    var groupName: String = null

    /**
      * Base path for API scanning
      */
    @BeanProperty
    var basePackage: JList[String] = new JArrayList[String]()

    /**
      * The base package for model scanning
      */
    @BeanProperty
    var modelBasePackage: JList[String] = new JArrayList[String]()

    /**
      * The URL path matcher to include in this docket
      */
    @BeanProperty
    var pathRegex: JList[String] = new JArrayList[String]()

    /**
      * the api info for this docket (all values will default to global apiInfo config)
      */
    @BeanProperty
    var apiInfo: ApiInfoConfig = new ApiInfoConfig

    /**
      * the contact info for this docket  (all values will default to global contact config)
      */
    @BeanProperty
    var contact: ContactConfig = new ContactConfig

    /**
      * the security type for this docket
      */
    @BeanProperty
    var securityType: String = null

    /**
      * Any additional models to add
      */
    @BeanProperty
    var additionalModels: JList[Class[_]] = new JArrayList[Class[_]]()

    /**
      * Model substitutions, expects the substitution class
      * to be named xxxDocumentation.
      *
      * @example To substitute for com.srivastavavivekggn.Primary, you should have a class called com.srivastavavivekggn.PrimaryDocumentation
      */
    var substituteModels: JMap[Class[_], JList[Class[_]]] = new JHashMap()

    def getSubstituteModels(): JMap[Class[_], JList[Class[_]]] = {

      if(!scalaMapped) {
        scalaMappedTypes.foreach(mappedType => {

          if (!substituteModels.containsKey(mappedType._1)) {
            substituteModels.put(mappedType._1, new JArrayList())
          }

          mappedType._2.foreach(c => substituteModels.get(mappedType._1).add(c))
        })

        scalaMapped = true
      }

      substituteModels
    }

    def setSubstituteModels(models: JMap[Class[_], JList[Class[_]]]): Unit = {
      substituteModels = models
    }


    private var scalaMapped = false
    private val scalaMappedTypes = Map(
      (classOf[Void], Seq(classOf[Unit], classOf[BoxedUnit])),
      (classOf[Integer], Seq(classOf[Int])),
      (classOf[java.lang.Boolean], Seq(classOf[Boolean])),
      (classOf[java.lang.Double], Seq(classOf[Double])),
      (classOf[java.lang.Float], Seq(classOf[Float])),
      (classOf[java.math.BigInteger], Seq(classOf[BigInt]))
    )
  }


  class SwaggerTag {

    @BeanProperty
    var name: String = null

    @BeanProperty
    var description: String = null
  }

  /**
    * Swagger security config
    */
  class SecurityConfig {

    /**
      * client id
      */
    @BeanProperty
    var clientId: String = null

    /**
      * client secret
      */
    @BeanProperty
    var clientSecret: String = null

    /**
      * realm
      */
    @BeanProperty
    var realm: String = null

    /**
      * app name
      */
    @BeanProperty
    var appName: String = null

    /**
      * api key value
      */
    @BeanProperty
    var apiKeyValue: String = null

    /**
      * api key vehicle
      */
    @BeanProperty
    var apiKeyVehicle: String = "header"

    /**
      * api key name
      */
    @BeanProperty
    var apiKeyName: String = "api_key"

    /**
      * scope separator
      */
    @BeanProperty
    var scopeSeparator: String = ","
  }

}
