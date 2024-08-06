package com.srivastavavivekggn.springboot.autoconfigure.swagger

import com.fasterxml.classmate.{ResolvedType, TypeResolver}
import com.google.common.base.Predicates
import com.srivastavavivekggn.scala.util.collection.{CollectionUtils => CU}
import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerAutoConfiguration._
import com.srivastavavivekggn.springboot.autoconfigure.swagger.SwaggerProperties.{ApiInfoConfig, ContactConfig, DocketConfig}
import com.srivastavavivekggn.springboot.autoconfigure.swagger.plugin.{MessageSourceModelBuilderPlugin, MessageSourceModelPropertyBuilderPlugin, MessageSourceOperationBuilderPlugin}
import com.srivastavavivekggn.springboot.autoconfigure.swagger.rules.AlternateRules.ScalaTypeRule
import io.swagger.annotations.ApiModel
import org.reflections.Reflections
import org.reflections.scanners.{SubTypesScanner, TypeAnnotationsScanner}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass, ConditionalOnProperty, ConditionalOnWebApplication}
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.context.{ApplicationContext, ApplicationListener, ConfigurableApplicationContext, MessageSource}
import springfox.documentation.RequestHandler
import springfox.documentation.builders.BuilderDefaults.defaultIfAbsent
import springfox.documentation.builders.{PathSelectors, RequestHandlerSelectors}
import springfox.documentation.schema.AlternateTypeRules
import springfox.documentation.schema.configuration.ObjectMapperConfigured
import springfox.documentation.service._
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spi.service.contexts.SecurityContext
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import com.google.common.base.Predicates.or

import java.util.function.Predicate
import java.util.stream.Collectors
import java.util.{Arrays => JArrays, List => JList, Map => JMap, Set => JSet}
import scala.collection.immutable.ArraySeq

/**
  * Configure swagger as long as this is a web application
  */
@Configuration
@EnableSwagger2
@ConditionalOnClass(Array(classOf[EnableSwagger2]))
@ConditionalOnWebApplication
@ConditionalOnProperty(prefix = "swagger", name = Array("enabled"), havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(Array(classOf[SwaggerProperties]))
class SwaggerAutoConfiguration {

  /**
    * reusable empty string val
    */
  @inline final val EMPTY = ""

  /**
    * Autowire in the props
    */
  @Autowired
  val properties: SwaggerProperties = null

  /**
    * Type resolver instance
    */
  @Autowired
  val typeResolver: TypeResolver = null


  /**
    * If the DefaultScalaModule is on the classpath, we need to register it with
    * the objectMapper created by Swagger
    *
    * @return the application listener
    */
  @Bean
  def objectMapperInitializer: ApplicationListener[ObjectMapperConfigured] = {
    new ApplicationListener[ObjectMapperConfigured] {

      // register all locally available modules
      def onApplicationEvent(event: ObjectMapperConfigured) = {
        event.getObjectMapper.findAndRegisterModules()
      }
    }
  }

  /**
    * Enable use of spring's message source to define model property documentation
    *
    * @param messages the message source
    * @return the model property builder plugin
    */
  @Bean
  @ConditionalOnBean(Array(classOf[MessageSource]))
  def modelPropertyPlugin(messages: MessageSource): MessageSourceModelPropertyBuilderPlugin = {
    new MessageSourceModelPropertyBuilderPlugin(messages, properties)
  }

  /**
    * Enable use of spring's message source to define model documentation
    *
    * @param messages the message source
    * @return the model builder plugin
    */
  @Bean
  @ConditionalOnBean(Array(classOf[MessageSource]))
  def modelPlugin(messages: MessageSource): MessageSourceModelBuilderPlugin = {
    new MessageSourceModelBuilderPlugin(messages, properties)
  }

  /**
    * Enable use of spring's message source to define operation documentation
    *
    * @param messages the message source
    * @return the operation builder plugin
    */
  @Bean
  @ConditionalOnBean(Array(classOf[MessageSource]))
  def operationPlugin(messages: MessageSource): MessageSourceOperationBuilderPlugin = {
    new MessageSourceOperationBuilderPlugin(messages)
  }

  /**
    * Defines any number of dockets for this swagger config
    *
    * @param appContext the application context
    */
  @Autowired
  def swaggerDocketSetup(appContext: ApplicationContext): Unit = {

    // we need to get the bean registry so we can register Docket beans
    val configContext = appContext.asInstanceOf[ConfigurableApplicationContext]
    val beanRegistry = configContext.getBeanFactory()

    // for each configured docket
    asScala(properties.dockets).foreach(registerDocket(_, beanRegistry))
  }

  /**
    *
    * @param config
    * @param beanRegistry
    */
  private def registerDocket(config: DocketConfig, beanRegistry: ConfigurableListableBeanFactory): Unit = {
    // now define the docket
    val d = new Docket(getDocType(config.documentationType))
      .select()
      .apis(getBasePackageConfig(config.basePackage))
      .paths(getSelectorConfig(config.pathRegex))
      .build()
      .groupName(config.groupName)
      .apiInfo(getApiInfo(config.apiInfo, config.contact))
      .alternateTypeRules(ScalaTypeRule()) // add generic scala type rule

    handleAdditionalModels(d, config)
    handleDirectModelSubstitutes(d, config)

    scanForApiModels(config.modelBasePackage) match {
      case Nil => // do nothing
      case head :: tail => d.additionalModels(head, tail: _*)
    }

    // configure any security necessary
    configureSecurity(d, config)

    // configure any tags
    configureTags(d, config)

    val beanName = s"swaggerDocket${config.groupName.capitalize}"

    // ensure this bean doesn't already exist in the bean registry
    if (beanRegistry.containsBean(beanName)) {
      throw new RuntimeException(s"Duplicate Swagger groupName found: $beanName")
    }

    // finally, register the docket bean
    beanRegistry.registerSingleton(beanName, d)
  }


  /**
    * Helper method to configure any tags
    * @param docket the docket
    * @param config the docket config
    */
  private def configureTags(docket: Docket, config: DocketConfig): Unit = {
    asScala(properties.tags).map(t => new Tag(t.name, t.description)) match {
      case Nil => // do nothing
      case head :: tail => docket.tags(head, tail: _*)
    }
  }

  /**
    * Configure security on this docket
    * @param docket the docket
    * @param config the docket config
    */
  private def configureSecurity(docket: Docket, config: DocketConfig): Unit = {

    config.securityType match {

      // use jwt security
      case JWT =>
        val securityReference = new SecurityReference(JWT, Array(new AuthorizationScope("global", "accessEverything")))
        val securityContext = SecurityContext.builder()
          .forPaths(PathSelectors.any())
          .securityReferences(JArrays.asList(securityReference))
          .build()

        docket.securitySchemes(JArrays.asList(new ApiKey(JWT, "Authorization", "header")))
        docket.securityContexts(JArrays.asList(securityContext))

      case "apiKey" =>

      case _ => // do nothing
    }
  }

  /**
    * Register any additional models
    *
    * @param d      the docket
    * @param config the docket config
    */
  private def handleAdditionalModels(d: Docket, config: DocketConfig): Unit = {

    // register any additional models
    asScala(config.additionalModels).map(c => typeResolver.resolve(c)) match {
      case Nil => // do nothing
      case head :: tail => d.additionalModels(head, tail: _*)
      case _ => // do nothing
    }
  }

  /**
    * Handle direct model substitutions
    *
    * @param d      the docket
    * @param config the docket configuration
    */
  private def handleDirectModelSubstitutes(d: Docket, config: DocketConfig): Unit = {
    asScala(config.getSubstituteModels())
      .foreach(entry => {
        val replacement = entry._1
        asScala(entry._2).foreach(c => {
          d.directModelSubstitute(c, replacement)
          d.alternateTypeRules(ScalaTypeRule(c, replacement, AlternateTypeRules.GENERIC_SUBSTITUTION_RULE_ORDER - 1000))
        })
      })
  }


  private def scanForApiModels(pkgs: JList[String]): List[ResolvedType] = {

    if (!pkgs.isEmpty) {
      try {
        Class.forName(classOf[Reflections].getName)
      }
      catch {
        case e: Exception => throw new RuntimeException("Reflections library required to scan for Models: org.reflections:reflections:0.9.+")
      }
    }

    asScala(pkgs).flatMap(p => {
      val result = new Reflections(p, new SubTypesScanner, new TypeAnnotationsScanner).getTypesAnnotatedWith(classOf[ApiModel])

      asScala(result)
        .filterNot(_.getSimpleName.endsWith("$"))
        .map(typeResolver.resolve(_))
    })
  }

  /**
    * Determine Documenation Type from the configuration
    *
    * @param dt the integer value for doctype
    * @return the swagger documentation type
    */
  private def getDocType(dt: Integer): DocumentationType = dt.toInt match {
    case 1 => DocumentationType.SWAGGER_12
    case 3 => DocumentationType.SPRING_WEB
    case _ => DocumentationType.SWAGGER_2
  }

  /**
    * Construct the ApiInfo from the provided configs
    *
    * @param config        the api info config
    * @param contactConfig the contact config
    * @return the ApiInfo objecet
    */
  private def getApiInfo(config: ApiInfoConfig, contactConfig: ContactConfig): ApiInfo = {
    new ApiInfo(
      getValue(config.title, properties.apiInfo.title, EMPTY),
      getValue(config.description, properties.apiInfo.description, EMPTY),
      getValue(config.version, properties.apiInfo.version, EMPTY),
      getValue(config.termsOfServiceUrl, properties.apiInfo.termsOfServiceUrl, EMPTY),
      getContactConfig(contactConfig),
      getValue(config.license, properties.apiInfo.license, EMPTY),
      getValue(config.licenseUrl, properties.apiInfo.licenseUrl, EMPTY),
      new java.util.ArrayList[VendorExtension[_]]()
    )
  }

  /**
    * Construct a Contact object from the config
    *
    * @param config the contact config
    * @return the Contact info
    */
  private def getContactConfig(config: ContactConfig): Contact = {
    new Contact(
      getValue(config.name, properties.contact.name, EMPTY),
      getValue(config.url, properties.contact.url, EMPTY),
      getValue(config.email, properties.contact.email, EMPTY)
    )
  }

  /**
    * Get the proper base package predicate from the passed config
    *
    * @param config the configuration
    * @return the RequestHandler predicate
    */
  private def getBasePackageConfig(config: JList[String]): Predicate[RequestHandler] = Option(config) match {

    // if we have a configured list
    case Some(lst) if !lst.isEmpty =>

      // map each base package string into a Predicate[RequestHandler]
      asScala(lst) match {
        case head :: Nil => RequestHandlerSelectors.basePackage(head)
        case head :: tail =>
          tail.foldLeft(RequestHandlerSelectors.basePackage(head))((l, r) => l.or(RequestHandlerSelectors.basePackage(r)))
      }

    // otherwise, return default selector
    case _ => RequestHandlerSelectors.basePackage("com.qh")
  }

  /**
    * Get the selector configuration for the given config
    *
    * @param config the config
    * @return the Predicate[String] of matching selectors
    */
  private def getSelectorConfig(config: JList[String]): Predicate[String] = Option(config) match {

    case Some(lst) if !lst.isEmpty =>
      // turn each entry into a regex path selector
      val selectors = asScala(lst).map(c => PathSelectors.regex(c))

      // map each base package string into a Predicate[RequestHandler]
      asScala(lst) match {
        case head :: Nil => PathSelectors.regex(head)
        case head :: tail =>
          tail.foldLeft(PathSelectors.regex(head))((l, r) => l.or(PathSelectors.regex(r)))
      }

    // nothing defined, use 'any' matcher
    case _ => PathSelectors.any()
  }

  /**
    * Gets the most specific value available from the config
    *
    * @param child   the child (defined on the docket config directly)
    * @param parent  the parent (defined outside of the docket - global config)
    * @param default the default value if others are missing
    * @tparam T the value type
    * @return the most specific value available
    */
  private def getValue[T](child: T, parent: T, default: T): T = defaultIfAbsent(
    child, defaultIfAbsent(parent, default)
  )


  private def asScala[T](lst: JList[T]): List[T] = {
    Option(lst).map(CU.asScalaListOrEmpty).getOrElse(List.empty)
  }

  private def asScala[T](lst: JSet[T]): Set[T] = {
    Option(lst).map(CU.asScalaSetOrEmpty).getOrElse(Set.empty)
  }

  private def asScala[K, V](mp: JMap[K, V]): Map[K, V] = {
    Option(mp).map(CU.asScalaMapOrEmpty).getOrElse(Map.empty)
  }

}


object SwaggerAutoConfiguration {

  final val JWT = "jwt"
  final val API_KEY = "apiKey"

}