package com.srivastavavivekggn.springboot.autoconfigure.web.tomcat

import com.srivastavavivekggn.scala.util.collection.CollectionUtils._
import org.apache.catalina.connector.{Request, Response}
import org.apache.catalina.valves.AbstractAccessLogValve.StringElement
import org.apache.catalina.valves.{AbstractAccessLogValve, AccessLogValve}
import org.apache.catalina.{Container, LifecycleState, Valve}
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.{ConditionalOnClass, ConditionalOnProperty, ConditionalOnWebApplication}
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.{Bean, Configuration}

import java.io.CharArrayWriter
import java.util.Date
import scala.jdk.CollectionConverters._

@Configuration
@ConditionalOnProperty(prefix = "server.tomcat.accesslog", name = Array("enabled"), matchIfMissing = false)
@ConditionalOnClass(Array(classOf[AccessLogValve]))
@ConditionalOnWebApplication
class TomcatAccessLogAutoConfiguration {

  /**
    * Create a servlet container customizer
    *
    * @return the customizer
    */
  @Bean
  def servletContainerCustomizer(delegates: java.util.List[TomcatAccessDelegate]): WebServerFactoryCustomizer[ConfigurableServletWebServerFactory] = {
    new WebServerFactoryCustomizer[ConfigurableServletWebServerFactory] {

      /**
        * Customize the tomcat container
        *
        * @param container the container
        */
      override def customize(container: ConfigurableServletWebServerFactory): Unit = Option(container) match {

        // if its a tomcat container
        case Some(c: TomcatServletWebServerFactory) =>

          // wrap the existing AccessLogValve with our delegating valve
          c.setEngineValves(
            (c.getEngineValves.asScala
              .map {
                case a: AccessLogValve => new TomcatAccessLogAutoConfiguration.AccessLogDelegatingValve(a, delegates.asScalaOrEmpty)
                case x: Valve => x
              }
              ).asJavaCollection
          )

        // other container type, do nothing
        case _ => ()
      }
    }
  }
}


object TomcatAccessLogAutoConfiguration {

  /**
    * Delegating access log valve -- wraps the real valve so we can add custom formats
    *
    * @param delegate the delegate valve
    */
  class AccessLogDelegatingValve(delegate: AccessLogValve,
                                 delegateFns: List[TomcatAccessDelegate]) extends AccessLogValve {

    // logger for this class
    private val logger = LoggerFactory.getLogger(getClass)

    // set pattern from the delegate
    this.setPattern(delegate.getPattern)

    /**
      * Main log entry point, we'll handle the formatting
      *
      * @param request  the request
      * @param response the response
      * @param time     the execution time
      */
    override def log(request: Request, response: Response, time: Long): Unit = {
      super.log(request, response, time)
    }

    /**
      * Log the formatted message out to file -- send this to the delegate
      *
      * @param message the message to log
      */
    override def log(message: CharArrayWriter): Unit = {
      delegate.log(message)
    }

    /**
      * Tell the delegate to execute the background process
      */
    override def backgroundProcess(): Unit = delegate.backgroundProcess()

    /**
      * Make sure the delegate gets a container ref also
      *
      * @param container the container
      */
    override def setContainer(container: Container): Unit = {
      super.setContainer(container)
      delegate.setContainer(container)
    }

    /**
      * Set the state
      *
      * @param state state
      */
    override def setState(state: LifecycleState): Unit = {
      setState(state, null)
    }

    /**
      * Set the state  and start/stop the delegate appropriately
      *
      * @param state the state
      * @param data  the data
      */
    override def setState(state: LifecycleState, data: scala.Any): Unit = {
      super.setState(state, data)

      if (state == LifecycleState.STARTING) {
        delegate.start()
        logger.info(s"Writing AccessLogs to ${Option(delegate.getDirectory).getOrElse("")}")
      }

      if (state == LifecycleState.STOPPING) {
        delegate.stop()
      }
    }

    /**
      * We're not actually doing IO (the delegate is) so we don't need to open any files
      */
    override def open(): Unit = {}

    /**
      * We can use our own custom AccessLogElements with parameters (e.g., %{xxx}i), or just delegate to super
      * for the built-in ones
      *
      * @param name    the name
      * @param pattern the pattern
      * @return the access log element
      */
    override def createAccessLogElement(name: String, pattern: Char): AbstractAccessLogValve.AccessLogElement = {
      pattern match {
        case p if delegateFns.exists(t => t.pattern == p && t.name == name) => new FunctionalUserElement(delegateFns.find(_.pattern == p).get)
        case 'C' if (name == "host") => new RequestHostElement
        case _ => super.createAccessLogElement(name, pattern)
      }
    }


    /**
      * We can use our own custom AccessLogElements with no params (e.g., %i), or just delegate to super
      * for the built-in ones
      *
      * @param pattern the pattern
      * @return the access log element
      */
    override def createAccessLogElement(pattern: Char): AbstractAccessLogValve.AccessLogElement = {
      pattern match {
        case '_' => new StringElement("_")
        case _ => super.createAccessLogElement(pattern)
      }
    }


    /**
      * Custom element that takes a configured TomcatAccessDelegate to do the actual work
      *
      * @param delegate the delegate
      */
    protected class FunctionalUserElement(delegate: TomcatAccessDelegate) extends AbstractAccessLogValve.AccessLogElement {
      override def addElement(buf: CharArrayWriter, date: Date, request: Request, response: Response, time: Long): Unit = {
        val result = delegate.process(request)
        buf.append(result)
      }
    }


    /**
      * Logger element for the request host
      */
    protected class RequestHostElement extends AbstractAccessLogValve.AccessLogElement {
      override def addElement(buf: CharArrayWriter,
                              date: Date,
                              request: Request,
                              response: Response, time: Long): Unit = {

        try {
          val path = Option(request.getServletPath)
          val url = Option(request.getRequestURL).map(_.toString)
            .map(u => u.startsWith("http") match {
              case true => u.substring(u.indexOf("://") + 3)
              case _ => u
            })
            .map(u => path.isDefined && u.contains(path.get) match {
              case true => u.substring(0, u.indexOf(path.get))
              case _ => u
            })

          buf.append(url.getOrElse("-"))
        }
        catch {
          // if an exception including NPE
          case e : Exception =>
            logger.error("Exception while adding entry to access log", e)
            buf.append("-")
        }
      }
    }
  }

}
