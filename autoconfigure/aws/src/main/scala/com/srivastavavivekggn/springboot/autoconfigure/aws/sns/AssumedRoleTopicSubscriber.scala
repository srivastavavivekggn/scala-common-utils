package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.SubscribeRequest
import software.amazon.awssdk.services.sqs.SqsClient

case class AssumedRoleTopicSubscriber(snsClient: SnsClient) extends TopicSubscriber {

  override def subscribeByName(topicName: String,
                               queueUrl: String,
                               sqs: SqsClient,
                               keepExisting: Boolean = true): Unit = throw new RuntimeException("Subscription by name not allowed when using assumed role")

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
    * Unsubscribe a queue from the given topic
    *
    * @param topicArn the topic ARN
    * @param queueArn the queue ARN
    */
  override def unsubscribeByArn(topicArn: String, queueArn: String): Unit = ()
}
