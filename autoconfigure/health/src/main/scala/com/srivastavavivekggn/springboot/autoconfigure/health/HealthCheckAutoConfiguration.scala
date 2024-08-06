package com.srivastavavivekggn.springboot.autoconfigure.health

import com.srivastavavivekggn.scala.util.TypeAlias.{JList, JMap}
import com.srivastavavivekggn.scala.util.collection.CollectionUtils._
import com.srivastavavivekggn.platform.heathcheck.HealthCheck.Level
import com.srivastavavivekggn.platform.heathcheck.{BuildInfo, GitInfo, HealthCheck, HealthCheckFailedException, HealthCheckWarnException}
import com.srivastavavivekggn.springboot.autoconfigure.health.HealthCheckAutoConfiguration.WrappedHealthIndicator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.boot.actuate.health.{HealthIndicator, Status}
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass, ConditionalOnMissingBean, ConditionalOnProperty}
import org.springframework.context.annotation.{Bean, Configuration}

/**
  * Auto-configuration for health checks
  *
  * This config will:
  *
  * - component-scan for all HealthCheck classes under <code>com.srivastavavivekggn.platform.heathcheck</code>
  *
  * - optionally convert (wrap) all SpringBoot Actuator health checks as HealthCheck instances
  *
  * - create the HealthCheckController bean with all HealthChecks
  *
  * @note <code>com.srivastavavivekggn.platform.heathcheck.HealthCheck</code> must be on the classpath
  */
@Configuration
@ConditionalOnClass(Array(classOf[HealthCheck]))
class HealthCheckAutoConfiguration {

  @Autowired(required = false)
  private val healthIndicators = new java.util.ArrayList[HealthIndicator]()

  /**
    * Wrap Spring Actuator's HealthIndicator(s) as our own HealthCheck(s)
    *
    * Disable this by setting {@ health.include.actuator=false in the properties
    *
    * @param beanFactory the bean factory
    * @param indicators  the HealthIndicators
    */
  @Autowired
  @ConditionalOnClass(Array(classOf[HealthIndicator]))
  @ConditionalOnBean(Array(classOf[HealthIndicator]))
  @ConditionalOnProperty(prefix = "health", name = Array("include.actuator"), matchIfMissing = true)
  def exposeActuatorEndpoints(beanFactory: ConfigurableListableBeanFactory): Unit = {

    healthIndicators.asScalaOrEmpty
      .map(WrappedHealthIndicator)
      .filterNot(hc => beanFactory.containsBean(s"healthCheck-${hc.getName}"))
      .foreach(hc => beanFactory.registerSingleton(s"healthCheck-${hc.getName}", hc))
  }

  /**
    * Expose public system metrics as a health indicator
    *
    * @param metrics the metrics
    * @return the health indicator for public metrics
    */
//  @Bean
//  @ConditionalOnBean(Array(classOf[SystemPublicMetrics]))
//  def exposeSystemPublicIndicator(metrics: SystemPublicMetrics): HealthCheck = new HealthCheck {
//
//    final val prefixes = Seq("classes", "gc", "heap", "nonheap", "processors", "system", "threads", "uptime")
//
//    override def getName: String = "System Metrics"
//
//    override def getDetails: String = "OK"
//
//    override def getLevel: Level = Level.HIGH
//
//    override def getDetailsMap: JMap[String, String] = {
//      val m = metrics.metrics().asInstanceOf[JLinkedHashSet[Metric[_]]].asScalaOrEmpty
//
//      val javaMap = new JTreeMap[String, String]()
//
//      m.map(x => x.getName -> s"${x.getValue}")
//        .toList
//        .filter(x => prefixes.exists(x._1.startsWith))
//        .sortBy(_._1.toLowerCase)
//        .foreach(k => javaMap.put(k._1, k._2))
//
//      javaMap
//    }
//
//    override def execute(): Unit = {
//      // can check thresholds here if needed
//    }
//  }

  /**
    * Register the health check controller with the list of all health checks
    *
    * @param checks the health checks
    * @return the controller
    */
  @Bean
  @ConditionalOnBean(Array(classOf[GitInfo], classOf[BuildInfo]))
  def healthCheckController1(checks: JList[HealthCheck],
                             gitInfo: GitInfo,
                             buildInfo: BuildInfo): HealthCheckController = {
    new HealthCheckController(checks, gitInfo, buildInfo)
  }

  @Bean
  @ConditionalOnBean(Array(classOf[GitInfo]))
  @ConditionalOnMissingBean(Array(classOf[BuildInfo]))
  def healthCheckController2(checks: JList[HealthCheck], gitInfo: GitInfo): HealthCheckController = {
    new HealthCheckController(checks, gitInfo)
  }

  @Bean
  @ConditionalOnMissingBean(Array(classOf[GitInfo], classOf[BuildInfo]))
  def healthCheckController3(checks: JList[HealthCheck]): HealthCheckController = {
    new HealthCheckController(checks)
  }

}


/**
  * The companion object
  */
object HealthCheckAutoConfiguration {


  case class WrappedHealthIndicator(indicator: HealthIndicator) extends HealthCheck {

    override def getLevel: HealthCheck.Level = Level.HIGH

    override def execute(): Unit = Option(indicator.health()) match {
      case None => throw new HealthCheckWarnException(this)
      case Some(health) if (health.getStatus != Status.UP) => throw new HealthCheckFailedException(this)
      case _ => // do nothing
    }

    override def getName: String = {
      Option(getDetailsMap.get("name")).getOrElse(indicator.getClass.getSimpleName.replace("HealthIndicator", ""))
    }

    override def getDetailsMap: JMap[String, String] = indicator.health().getDetails.asScalaOrEmpty.map(e => e._1 -> s"${e._2}").asJavaOrEmpty
  }

}
