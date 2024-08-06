package com.srivastavavivekggn.springboot.autoconfigure.aws.sqs

import com.srivastavavivekggn.scala.util.TypeAlias.JList
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.platform.heathcheck.HealthCheck.Level
import com.srivastavavivekggn.platform.heathcheck.{HealthCheck, HealthCheckFailedException}
import com.srivastavavivekggn.springboot.autoconfigure.aws.AwsCredentialsAutoConfiguration
import com.srivastavavivekggn.springboot.autoconfigure.aws.sns.{AssumedRoleTopicSubscriberFactory, AwsSNSAutoConfiguration, NoOpTopicSubscriber, TopicSubscriber}
import com.typesafe.scalalogging.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass, ConditionalOnMissingBean, ConditionalOnProperty}
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.{Bean, Configuration}
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sns.model.{Topic => SnsTopic}
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.{CreateQueueRequest, GetQueueUrlRequest, QueueDoesNotExistException}

import java.util.concurrent.atomic.AtomicReference
import scala.util.{Failure, Success, Try}

/**
  * Auto configuration for Amazon SQS with optional support
  * for creating a JmsTemplate that uses the SQS connection factory
  */
@Configuration
@ConditionalOnBean(Array(classOf[SqsClient], classOf[AwsCredentialsProvider]))
@ConditionalOnProperty(prefix = "aws.sqs", name = Array("enabled"), havingValue = "true", matchIfMissing = false)
@AutoConfigureAfter(Array(classOf[AwsCredentialsAutoConfiguration], classOf[AwsSNSAutoConfiguration]))
@EnableConfigurationProperties(Array(classOf[SQSPropertyModels.AwsSQSProperties]))
class AwsSQSAutoConfiguration {

  /**
    * wire in the properties
    */
  @Autowired
  val properties: SQSPropertyModels.AwsSQSProperties = null

  /**
    * Optional topic subscriber for handling assumed roles
    */
  @Autowired(required = false)
  val subscriberFactory: AssumedRoleTopicSubscriberFactory = null

  /**
    * logger for this class
    */
  private val logger = Logger[AwsSQSAutoConfiguration]

  /**
    * Dummy topic subscriber - should only get this if SNS is not setup properly
    *
    * @return the topic subscriber
    */
  @Bean
  @ConditionalOnMissingBean(Array(classOf[TopicSubscriber]))
  def dummyTopicSubscriber(): TopicSubscriber = new NoOpTopicSubscriber

  /**
    * SQS Health Check
    *
    * @param SqsClient the amazon sqs client
    * @return the health check
    */
  @Bean
  @ConditionalOnClass(Array(classOf[SqsClient], classOf[HealthCheck]))
  @ConditionalOnBean(Array(classOf[SqsClient]))
  @ConditionalOnMissingBean(name = Array("sqsHealthCheck"))
  def sqsHealthCheck(sqsClient: SqsClient): HealthCheck = new HealthCheck {

    private val resultSet: AtomicReference[JList[String]] = new AtomicReference[JList[String]]()

    override def execute(): Unit = {
      try {
        resultSet.set(
          sqsClient.listQueues().queueUrls()
        )
      }
      catch {
        case e: Exception => throw new HealthCheckFailedException(this, e)
      }
    }

    override def getDetails = "Ok"

    override def getLevel: Level = Level.MEDIUM
  }


  /**
    * SNS Health Check
    *
    * @param SnsClient the sns health check
    * @return the health check
    */
  @Bean
  @ConditionalOnClass(Array(classOf[SnsClient], classOf[HealthCheck]))
  @ConditionalOnBean(Array(classOf[SnsClient]))
  @ConditionalOnMissingBean(name = Array("snsHealthCheck"))
  def snsHealthCheck(snsClient: SnsClient): HealthCheck = new HealthCheck {

    private val resultSet: AtomicReference[JList[SnsTopic]] = new AtomicReference[JList[SnsTopic]]()

    override def execute(): Unit = {
      try {
        resultSet.set(
          snsClient.listTopics().topics()
        )
      }
      catch {
        case e: Exception => throw new HealthCheckFailedException(this, e)
      }
    }

    override def getDetails = "Ok"

    override def getLevel: Level = Level.MEDIUM
  }

  /**
    * Ensure each configured queue exists and optionally create on missing, also subscribe
    * to all topics listed in config
    *
    * @param sqs             the sqs client
    * @param topicSubscriber the topic subscriber
    */
  @Bean
  @ConditionalOnBean(Array(classOf[SqsClient], classOf[TopicSubscriber]))
  def ensureQueues(sqs: SqsClient,
                   topicSubscriber: TopicSubscriber): List[SQSPropertyModels.QueueProperties] = {

    // get all the queue URLs
    CollectionUtils.asScalaMapOrEmpty(properties.queues).values.toList
      .map(queue => {
        val queueUrl = Try {
          sqs.getQueueUrl(GetQueueUrlRequest.builder().queueName(queue.name).build())
        } match {

          // got the queue result, just get the URL
          case Success(url) => Option(url.queueUrl())

          // failed to get the queue because it doesn't exist
          case Failure(_: QueueDoesNotExistException) if queue.createIfMissing =>
            val newUrl = sqs.createQueue(
              CreateQueueRequest.builder().queueName(queue.name).build()
            ).queueUrl()

            Option(newUrl)

          // failed for some other reason
          case Failure(ex) =>
            logger.error(s"Could not find/create queue with name ${queue.name}", ex)
            None
        }

        // subscribe to topics
        queueUrl.foreach(u => subscribeTopics(u, CollectionUtils.asScalaListOrEmpty(queue.topics), topicSubscriber, sqs))

        // simply return the queue
        queue
      })
  }


  /**
    * Handle topic subscriptions. Method will only subscribe the queue to topics that it's not already
    * subscribed to. If the queue is already subscribed to the topic we will just skip over it. Also if we cannot
    * find a topic by the specified name we will ignore it.
    *
    * @param queueUrl        the queue url
    * @param topics          the topics to subscribe to
    * @param topicSubscriber the topic subscriber
    * @param sqs             the sqs client
    */
  private def subscribeTopics(queueUrl: String,
                              topics: List[SQSPropertyModels.Topic],
                              topicSubscriber: TopicSubscriber,
                              sqs: SqsClient): Unit = {

    // map all the topics into ARNs and get the appropriate TopicSubscriber instance for each
    val mappedTopics = mapTopics(queueUrl, topics, topicSubscriber)

    mappedTopics.foreach {

      // already subscribed to this topic
      case (ts: TopicSubscriber, arn: String) if subscriptionExists(arn, queueUrl, ts) =>
        logger.info(s"Queue $queueUrl is already subscribed to topic $arn")

      // not already subscribed, do it now
      case (subscriber: TopicSubscriber, arn: String) =>
        try {
          subscriber.subscribeByArn(arn, queueUrl, sqs, keepExisting = true)
          logger.info(s"Subscribed queue: $queueUrl to topic: $arn")
        }
        catch {
          case e: Exception => logger.error(s"Could not subscribe queue $queueUrl to topic $arn", e)
        }
    }
  }

  /**
    * Map the topics with the appropriate topic subscriber
    *
    * @param queueUrl        the queue URL
    * @param topics          the list of incoming topics
    * @param topicSubscriber the topic subscriber
    * @return List[(TopicSubscriber, String)] the topic subscriber with the topic ARN
    */
  private def mapTopics(queueUrl: String,
                        topics: List[SQSPropertyModels.Topic],
                        topicSubscriber: TopicSubscriber): List[(TopicSubscriber, String)] = {
    topics.map(t => mapTopicToSubscriber(t, queueUrl, topicSubscriber))
  }

  /**
    * Map a single topic to a subscriber
    *
    * @param t               the topic
    * @param queueUrl        the queue url
    * @param topicSubscriber the topic subscriber
    * @return the tuple of subscriber and arn
    */
  private def mapTopicToSubscriber(t: SQSPropertyModels.Topic,
                                   queueUrl: String,
                                   topicSubscriber: TopicSubscriber): (TopicSubscriber, String) = t match {

    // the topic has an ARN and has a role and subscriber factory exists
    case t: SQSPropertyModels.Topic if t.isArn && t.hasRole && subscriberFactory != null =>

      // create subscriber
      val subscriber = subscriberFactory.createTopicSubscriber(t.roleArn, t.roleSessionName)

      // verify topic exists
      subscriber.findTopicByArn(t.arn)
        .map(_ => (subscriber, t.arn))
        .getOrElse(logMissingTopic(t.arn, queueUrl))

    // already have an ARN
    case t: SQSPropertyModels.Topic if t.isArn =>

      // verify topic exists
      topicSubscriber.findTopicByArn(t.arn)
        .map(_ => (topicSubscriber, t.arn))
        .getOrElse(logMissingTopic(t.arn, queueUrl))

    // topic has a name only
    case t: SQSPropertyModels.Topic if t.name != null =>

      // verify topic exists
      topicSubscriber.findTopicByName(t.name)
        .map(foundTopic => (topicSubscriber, foundTopic.topicArn()))
        .getOrElse(logMissingTopic(t.name, queueUrl))

    // not enough info configured
    case _ => throw new RuntimeException(s"Not enough information to subscribe queue $queueUrl to topic")
  }

  /**
    * Log out missing topic and return NoOp subscriber
    *
    * @param nameOrArn the name or arn
    * @param queueUrl  the queue url we're trying to subscribe
    * @return the subscriber and name or arn value
    */
  private def logMissingTopic(nameOrArn: String, queueUrl: String): (TopicSubscriber, String) = {
    logger.warn(s"Couldn't find a topic $nameOrArn to subscribe queue $queueUrl")
    (new NoOpTopicSubscriber, nameOrArn)
  }

  /**
    * Check if the queue is currently subscribed to the topic
    *
    * @param topicArn the topic ARN
    * @param queue    the queue ARN or URL
    * @return Boolean true if the queue is subscribed to the topic otherwise false
    */
  def subscriptionExists(topicArn: String, queue: String, ts: TopicSubscriber): Boolean = {
    ts.findSubscriptionByTopicAndQueue(topicArn, queue).isDefined
  }
}
