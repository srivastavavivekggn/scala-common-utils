package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import com.srivastavavivekggn.scala.util.TypeAlias.{JHashMap, JMap}
import com.srivastavavivekggn.scala.util.collection.CollectionUtils._
import com.srivastavavivekggn.springboot.autoconfigure.aws.AwsCredentialsAutoConfiguration
import com.srivastavavivekggn.springboot.autoconfigure.aws.sns.AwsSNSAutoConfiguration.AwsSNSProperties
import com.srivastavavivekggn.springboot.autoconfigure.aws.sts.AwsStsAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnProperty}
import org.springframework.boot.context.properties.{ConfigurationProperties, EnableConfigurationProperties}
import org.springframework.context.annotation.{Bean, Configuration}
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.providers.AwsRegionProvider
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.CreateTopicRequest
import software.amazon.awssdk.services.sts.StsClient

import java.lang.{Boolean => JBoolean}
import scala.beans.BeanProperty

@Configuration
@ConditionalOnBean(Array(classOf[SnsClient], classOf[AwsCredentialsProvider]))
@ConditionalOnProperty(prefix = "aws.sns", name = Array("enabled"), havingValue = "true", matchIfMissing = false)
@AutoConfigureAfter(Array(classOf[AwsCredentialsAutoConfiguration], classOf[AwsStsAutoConfiguration]))
@EnableConfigurationProperties(Array(classOf[AwsSNSAutoConfiguration.AwsSNSProperties]))
class AwsSNSAutoConfiguration {

  /**
    * Create a topic subscriber bean -- this enables us to subscribe to a topic by name instead of ARN
    *
    * For example, to subscribe to
    * arn:aws:sns:us-east-1:606747817157:dev-ap-assessments-completed
    *
    * You simply need to know "dev-ap-assessments-completed"
    *
    * @param snsClient the sns service
    * @return the topic subscriber
    */
  @Bean
  def topicSubscriber(snsClient: SnsClient): TopicSubscriber = DefaultTopicSubscriber(snsClient)


  /**
    * Create the topic subscriber factory IFF we have an sts client
    *
    * @param sts            the sts clieint
    * @param regionProvider the region provider
    * @return the topic subscriber factory
    */
  @Bean
  @ConditionalOnBean(Array(classOf[StsClient]))
  def assumedRoleTopicSubscriberFactory(sts: StsClient,
                                        regionProvider: AwsRegionProvider): AssumedRoleTopicSubscriberFactory = {
    AssumedRoleTopicSubscriberFactory(sts, regionProvider.getRegion)
  }


  /**
    * Ensure all topics have been created properly
    *
    * @param sns the sns client
    * @return the
    */
  @Bean
  @ConditionalOnBean(Array(classOf[SnsClient]))
  def ensureTopics(sns: SnsClient, snsProperties: AwsSNSProperties): JMap[String, String] = {

    // we create this here so caching of topics is not an issue if we create new ones
    val topicSubscriber = DefaultTopicSubscriber(sns)

    snsProperties.topics.asScalaOrEmpty.values.foreach(topicName =>
      topicSubscriber.findTopicByName(topicName) match {
        case None => sns.createTopic(CreateTopicRequest.builder().name(topicName).build())
        case Some(topic) => // do nothing
      })

    // we have to return something
    snsProperties.topics
  }
}


object AwsSNSAutoConfiguration {

  @ConfigurationProperties(prefix = "aws.sns")
  class AwsSNSProperties {

    @BeanProperty
    var enabled: JBoolean = true

    @BeanProperty
    var topics: JMap[String, String] = new JHashMap[String, String]()
  }
}
