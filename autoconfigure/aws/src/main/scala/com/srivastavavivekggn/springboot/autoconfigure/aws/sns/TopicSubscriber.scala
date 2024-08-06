package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{ListSubscriptionsByTopicRequest, ListSubscriptionsByTopicResponse, ListTopicsRequest, ListTopicsResponse, Subscription, Topic}
import software.amazon.awssdk.services.sqs.SqsClient

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

trait TopicSubscriber {

  def snsClient: SnsClient

  /**
    * Subscribe the queue to the given topic
    *
    * @param topicName    the name of the topic
    * @param queueUrl     the queue url
    * @param sqs          the sqs client
    * @param keepExisting true to keep existing subscriptions, false to overwrite
    */
  def subscribeByName(topicName: String,
                      queueUrl: String,
                      sqs: SqsClient,
                      keepExisting: Boolean): Unit

  /**
    * Subscribe the given queue to the topic
    *
    * @param topicArn     the topic ARN
    * @param queueUrl     the queue url
    * @param sqs          the sqs client
    * @param keepExisting true to keep existing subscriptions, false to overwrite
    */
  def subscribeByArn(topicArn: String,
                     queueUrl: String,
                     sqs: SqsClient,
                     keepExisting: Boolean): Unit

  /**
    * Unsubscribe a queue from the given topic
    *
    * @param topicArn the topic ARN
    * @param queueArn the queue ARN
    */
  def unsubscribeByArn(topicArn: String,
                       queueArn: String): Unit

  /**
    * Lookup a topic by name
    *
    * @param topicName the topic name
    * @return the topic
    */
  def findTopicByName(topicName: String): Option[Topic] =  {
    getAllTopics().find(_.topicArn().endsWith(topicName))
  }

  /**
    * Lookup a topic by ARN
    *
    * @param topicArn the topic ARN
    * @return the topic
    */
  def findTopicByArn(topicArn: String): Option[Topic] = {
    getAllTopics().find(_.topicArn().equalsIgnoreCase(topicArn))
  }

  /**
    * Find a current subscription based on the topic and queue
    *
    * @param topicArn the topic ARN
    * @param queue    the queue ARN or URL
    * @return Option[Subscription] the subscription if any found
    */
  def findSubscriptionByTopicAndQueue(topicArn: String, queue: String): Option[Subscription] = {
    getSubscriptionsForTopic(topicArn).find(sub => {
      sub.endpoint().equalsIgnoreCase(queue) || queue.endsWith(sub.endpoint().split(":").takeRight(2).mkString("/"))

    })
  }

  /**
    * Recursive call to get all of the subscriptions for the given topic
    *
    * @param topicArn      the topic arn
    * @param subResult     the optional subscription result
    * @param subscriptions the collection of subscriptions
    * @return the list of subscriptions
    */
  @tailrec
  private final def getSubscriptionsForTopic(topicArn: String,
                                             subResult: Try[Option[ListSubscriptionsByTopicResponse]] = Success(None),
                                             subscriptions: List[Subscription] = List.empty): List[Subscription] = {

    // check the subscription result
    subResult match {

      // we have results AND a next token
      case Success(Some(result)) if Option(result.nextToken()).isDefined =>

        val next = Try(Option(snsClient.listSubscriptionsByTopic(
          ListSubscriptionsByTopicRequest.builder().topicArn(topicArn).nextToken(result.nextToken()).build())
        ))

        getSubscriptionsForTopic(
          topicArn,
          next,
          subscriptions ++ CollectionUtils.asScalaListOrEmpty(result.subscriptions())
        )

      // we have some result but no next token (we're done)
      case Success(Some(result)) =>
        subscriptions ++ CollectionUtils.asScalaListOrEmpty(result.subscriptions())

      // we don't have any results yet, make our first call out
      case Success(None) =>
        getSubscriptionsForTopic(topicArn, Try(Option(
          snsClient.listSubscriptionsByTopic(ListSubscriptionsByTopicRequest.builder().topicArn(topicArn).build())
        )))

      // some failure happened, just return what we have at this point
      case Failure(_) => subscriptions
    }
  }


  /**
    * Recursive call to get all of the topics
    *
    * @param subResult the optional topic result
    * @param topics    the collection of topics
    * @return the list of topics
    */
  @tailrec
  private final def getAllTopics(subResult: Try[Option[ListTopicsResponse]] = Success(None),
                                 topics: List[Topic] = List.empty): List[Topic] = {

    // check the topic result
    subResult match {

      // we have results AND a next token
      case Success(Some(result)) if Option(result.nextToken()).isDefined =>
        val next = Try(Option(snsClient.listTopics(
          ListTopicsRequest.builder().nextToken(result.nextToken()).build()
        )))

        getAllTopics(next, topics ++ CollectionUtils.asScalaListOrEmpty(result.topics()))

      // we have some result but no next token (we're done)
      case Success(Some(result)) =>
        topics ++ CollectionUtils.asScalaListOrEmpty(result.topics())

      // we don't have any results yet, make our first call out
      case Success(None) =>
        getAllTopics(Try(Option(snsClient.listTopics())))

      // some failure happened, just return what we have at this point
      case Failure(_) => topics
    }
  }

}
