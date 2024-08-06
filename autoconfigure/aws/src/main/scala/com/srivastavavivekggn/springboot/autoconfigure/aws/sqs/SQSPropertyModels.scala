package com.srivastavavivekggn.springboot.autoconfigure.aws.sqs

import com.srivastavavivekggn.scala.util.TypeAlias._
import org.springframework.boot.context.properties.ConfigurationProperties

import scala.beans.BeanProperty

object SQSPropertyModels {

  @ConfigurationProperties(prefix = "aws.sqs")
  class AwsSQSProperties {

    @BeanProperty
    var maxMessages: Integer = 3

    @BeanProperty
    var enabled: JBoolean = true

    @BeanProperty
    var queues: JMap[String, QueueProperties] = new JHashMap[String, QueueProperties]()
  }

  class QueueProperties {

    /**
      * The queue name
      */
    @BeanProperty
    var name: String = null

    /**
      * Flag to create queue if missing
      */
    @BeanProperty
    var createIfMissing: JBoolean = true

    /**
      * The topics this queue should subscribe to
      */
    @BeanProperty
    var topics: JList[Topic] = new JArrayList[Topic]()
  }

  /**
    * Topic model
    */
  class Topic {

    @BeanProperty
    var name: String = null

    @BeanProperty
    var arn: String = null

    @BeanProperty
    var roleArn: String = null

    @BeanProperty
    var roleSessionName: String = null

    def isArn: Boolean = arn != null

    def hasRole: Boolean = roleArn != null && roleSessionName != null

    override def toString: String = Option(name).orElse(Option(arn)).getOrElse(" - ")
  }
}
