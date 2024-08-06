package com.srivastavavivekggn.scala.util.properties

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.env.{PropertiesPropertySource, PropertySource}
import org.springframework.core.io.support.{EncodedResource, PropertySourceFactory}

/**
  * Enables loading/parsing YAML files using @PropertySource annotations.
  *
  * \@PropertySource(value = "classpath:foo.yml", factory = YamlPropertySourceFactory.class)
  * class MyClass {}
  */
class YamlPropertySourceFactory extends PropertySourceFactory {
  override def createPropertySource(name: String, resource: EncodedResource): PropertySource[_] = {

    val factory = new YamlPropertiesFactoryBean
    factory.setResources(resource.getResource)

    val properties = factory.getObject

    new PropertiesPropertySource(resource.getResource.getFilename, properties)
  }
}
