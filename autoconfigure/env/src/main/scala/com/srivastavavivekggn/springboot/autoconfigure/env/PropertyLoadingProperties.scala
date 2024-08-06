package com.srivastavavivekggn.springboot.autoconfigure.env

import com.srivastavavivekggn.scala.util.TypeAlias.{JArrayList, JList}
import org.springframework.boot.context.properties.ConfigurationProperties

import scala.beans.BeanProperty

/**
  * Property configuration for loading property files into the Environment
  */
@ConfigurationProperties(prefix = "properties")
class PropertyLoadingProperties {

  /**
    * The additional files to load
    */
  @BeanProperty
  val files: JList[String] = new JArrayList[String]()
}
