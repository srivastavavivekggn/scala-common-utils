package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import com.typesafe.scalalogging.StrictLogging
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{SubscribeRequest, UnsubscribeRequest}
import software.amazon.awssdk.services.sqs.SqsClient

case class DefaultTopicSubscriber(snsClient: SnsClient) extends TopicSubscriber with StrictLogging {

  override def subscribeByName(topicName: String,
                               queueUrl: String,
                               sqs: SqsClient,
                               keepExisting: Boolean = true): Unit = {
    findTopicByName(topicName)
      .map(_.topicArn())
      .foreach(subscribeByArn(_, queueUrl, sqs, keepExisting))
  }

  override def subscribeByArn(topicArn: String,
                              queueUrl: String,
                              sqs: SqsClient,
                              keepExisting: Boolean = true): Unit = {
    snsClient.subscribe(
      SubscribeRequest.builder()
        .endpoint(queueUrl)
        .protocol("sqs")
        .topicArn(topicArn)
        .build()
    )
  }

  /**
    * Unsubscribe a queue from a topic
    *
    * @param topicArn the topic ARN
    * @param queueArn the queue ARN
    */
  override def unsubscribeByArn(topicArn: String,
                                queueArn: String): Unit = try {

    findSubscriptionByTopicAndQueue(topicArn, queueArn).map(sub => {
      logger.info(s"Unsubscribing queue $queueArn from topic $topicArn")
      snsClient.unsubscribe(
        UnsubscribeRequest.builder()
          .subscriptionArn(sub.subscriptionArn())
          .build()
      )
    })
  } catch {
    case e: Exception => logger.error(s"Error while unsubscribing queue $queueArn from topic $topicArn")
  }

}
