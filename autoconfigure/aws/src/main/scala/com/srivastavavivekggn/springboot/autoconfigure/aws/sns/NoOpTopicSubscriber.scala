package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import com.typesafe.scalalogging.StrictLogging
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{Subscription, Topic}
import software.amazon.awssdk.services.sqs.SqsClient

class NoOpTopicSubscriber extends TopicSubscriber with StrictLogging {

  override val snsClient: SnsClient = null

  override def subscribeByName(topicName: String, queueUrl: String, sqs: SqsClient, keepExisting: Boolean = true): Unit = {
    logger.warn(s"Fake topic subscription for $queueUrl on topic $topicName")
  }

  override def subscribeByArn(topicArn: String, queueUrl: String, sqs: SqsClient, keepExisting: Boolean = true): Unit = {
    logger.warn(s"Fake topic subscription for $queueUrl on topic $topicArn")
  }

  /**
    * Unsubscribe a queue from the given topic
    *
    * @param topicArn the topic ARN
    * @param queueArn the queue URL
    */
  override def unsubscribeByArn(topicArn: String, queueArn: String): Unit = {
    logger.warn(s"Fake unsubscribe $queueArn from $topicArn")
  }

  /**
    * Lookup a topic by name
    *
    * @param topicName the topic name
    * @return the topic
    */
  override def findTopicByName(topicName: String): Option[Topic] = None

  /**
    * Lookup a topic by ARN
    *
    * @param topicArn the topic ARN
    * @return the topic
    */
  override def findTopicByArn(topicArn: String): Option[Topic] = None

  /**
    * Find a current subscription based on the topic and queue
    *
    * @param topicArn the topic ARN
    * @param queueArn the queue ARN
    * @return Option[Subscription] the subscription if any found
    */
  override def findSubscriptionByTopicAndQueue(topicArn: String, queueArn: String): Option[Subscription] = None
}
