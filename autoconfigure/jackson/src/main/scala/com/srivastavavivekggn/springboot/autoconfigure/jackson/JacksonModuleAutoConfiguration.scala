package com.srivastavavivekggn.springboot.autoconfigure.jackson

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.{Module, ObjectMapper}
import com.srivastavavivekggn.scala.util.TypeAlias.JList
import com.srivastavavivekggn.scala.util.collection.CollectionUtils.asScalaListOrEmptyConverter
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.{ConditionalOnClass, ConditionalOnMissingBean}
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration
import org.springframework.context.annotation.{Bean, Configuration}

import java.util.Locale

/**
  * Auto-configuration for Jackson's scala module.
  */
@Configuration
@AutoConfigureAfter(Array(classOf[JacksonAutoConfiguration]))
@ConditionalOnClass(Array(classOf[ObjectMapper]))
class JacksonModuleAutoConfiguration {

  /**
    * Define object mapper if one is not already defined
    *
    * @return the jackson mapper
    */
  @Bean
  @ConditionalOnMissingBean(Array(classOf[ObjectMapper]))
  def jacksonMapper: ObjectMapper = new ObjectMapper()


  /**
   * If we're using mongo and jackson, add serializer to output ObjectIds
   */
  @Bean
  @ConditionalOnClass(name = Array("org.bson.types.ObjectId"))
  def mongoObjectIdSerializer(mappers: JList[ObjectMapper]): Module = {

    val module = new SimpleModule()
    module.addSerializer(classOf[ObjectId], new MongoObjectIdSerializer)

    // register this module on all mappers
    mappers.asScalaOrEmpty.foreach(_.registerModule(module))

    module
  }

  @Bean
  def simpleTypesSerializer(mappers: JList[ObjectMapper]): Module = {

    val module = new SimpleModule()
    module.addSerializer(classOf[Locale], new LocaleSerializer)
    module.addDeserializer(classOf[Locale], new LocaleDeserializer)

    // register this module on all mappers
    mappers.asScalaOrEmpty.foreach(_.registerModule(module))

    module
  }

  /**
    * Register the scala module on all object mappers configured
    *
    * @param mappers the object mappers
    */
  @Autowired
  def findAndRegisterModules(mappers: JList[ObjectMapper]): Unit = {

    mappers.asScalaOrEmpty match {

      // empty
      case Nil => // do nothing, no mappers

      // single item
      case head :: Nil => head.findAndRegisterModules()

      // multiple items
      case head :: tail =>
        // find modules once
        val modules = ObjectMapper.findModules()

        // register on head
        head.registerModules(modules)

        // register on rest
        tail.foreach(_.registerModules(modules))
    }
  }
}
