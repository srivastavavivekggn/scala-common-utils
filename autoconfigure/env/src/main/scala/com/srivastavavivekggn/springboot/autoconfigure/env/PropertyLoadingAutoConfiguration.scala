package com.srivastavavivekggn.springboot.autoconfigure.env

import com.srivastavavivekggn.scala.util.collection.CollectionUtils._
import com.srivastavavivekggn.platform.heathcheck.{BuildInfo, GitInfo}
import com.srivastavavivekggn.springboot.autoconfigure.health.HealthCheckAutoConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass, ConditionalOnMissingBean, ConditionalOnResource}
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration
import org.springframework.boot.autoconfigure.{AutoConfigureAfter, AutoConfigureBefore}
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.info.{BuildProperties, GitProperties}
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.core.env.{ConfigurableEnvironment, StandardEnvironment}
import org.springframework.core.io.support.{PropertiesLoaderUtils, ResourcePropertySource}
import org.springframework.core.io.{ClassPathResource, FileSystemResource}

import java.util.{Date, Properties}

@Configuration
@AutoConfigureBefore(Array(classOf[HealthCheckAutoConfiguration]))
@AutoConfigureAfter(Array(classOf[ProjectInfoAutoConfiguration]))
@EnableConfigurationProperties(Array(classOf[PropertyLoadingProperties]))
class PropertyLoadingAutoConfiguration {

  /**
    * Loads any number of property sources configured. They will be loaded ABOVE any application.yml or
    * profile-specific yaml files.  The list of files is loaded in order, with the last file having
    * precedence over the first file.
    *
    * @param props the configured properties
    * @param env   the spring environment
    */
  @Autowired
  def loadPropertySources(props: PropertyLoadingProperties,
                          env: ConfigurableEnvironment): Unit = {

    val addAfter = Seq(
      StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
      StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME
    ).find(env.getPropertySources.contains)

    props.files.asScalaOrEmpty
      .filter(Option(_).isDefined)
      .map(file => file.startsWith("classpath:") match {
        case true => new ClassPathResource(file.substring(10))
        case _ => new FileSystemResource(file)
      })
      .filter(_.exists())
      .map(resource => new ResourcePropertySource(resource))
      .filterNot(_ == null)
      .foreach(resource => {
        env.getPropertySources.addAfter(addAfter.getOrElse(""), resource)
      })
  }
}


/**
  * Companion for property loading auto config
  */
object PropertyLoadingAutoConfiguration {

  /**
    * Configure build properties
    */
  @Configuration
  class BuildInfoPropertiesAutoConfiguration {

    /**
      * If we don't already have a BuildProperties bean, AND we have a build-info file, create it now
      *
      * We likely won't get here since Spring should already handle creating this in ProjectInfoAutoConfiguration
      *
      * @return the build properties
      */
    @Bean
    @ConditionalOnMissingBean(Array(classOf[BuildProperties]))
    @ConditionalOnResource(resources = Array("classpath:META-INF/build-info.properties"))
    def buildProperties: BuildProperties = {

      val resource = new ClassPathResource("META-INF/build-info.properties")
      val source = PropertiesLoaderUtils.loadProperties(resource)
      val target = new Properties()
      val prefix = "build."

      // we need to strip off the leading "git."
      source
        .stringPropertyNames()
        .asScalaOrEmpty
        .filter(_.startsWith(prefix))
        .foreach(key => {
          target.setProperty(key.substring(prefix.length), source.getProperty(key))
        })

      new BuildProperties(target)
    }


    /**
      * Expose the BuildProperties bean to the SC HealthCheck if it is on the classpath
      *
      * @param buildProperties the buildProperties from Spring
      * @return the BuildInfo bean for healthcheck
      */
    @Bean
    @ConditionalOnBean(Array(classOf[BuildProperties]))
    @ConditionalOnClass(Array(classOf[BuildInfo]))
    def buildInfo(buildProperties: BuildProperties): BuildInfo = {
      new BuildInfo(
        buildProperties.getVersion,
        buildProperties.getName
      )
    }
  }


  /**
    * Configure Git properties for this build
    */
  @Configuration
  class GitPropertiesAutoConfiguration {

    /**
      * If no GitProperties is defined, we'll load it ourselves
      *
      * Generally, this would already be handled by ProjectInfoAutoConfiguration
      *
      * @return the GitProperties bean
      */
    @Bean
    @ConditionalOnMissingBean(Array(classOf[GitProperties]))
    @ConditionalOnResource(resources = Array("classpath:git.properties"))
    def gitProperties: GitProperties = {

      val resource = new ClassPathResource("git.properties")
      val source = PropertiesLoaderUtils.loadProperties(resource)
      val target = new Properties()
      val prefix = "git."

      // we need to strip off the leading "git."
      source
        .stringPropertyNames()
        .asScalaOrEmpty
        .filter(_.startsWith(prefix))
        .foreach(key => {
          target.setProperty(key.substring(prefix.length), source.getProperty(key))
        })

      new GitProperties(target)
    }

    /**
      * Use the GitProperties bean to expose GitInfo for the SC HealthCheck (as long as it's on the classpath)
      *
      * @param gitProps the git properties bean
      * @return the GitInfo for health checking
      */
    @Bean
    @ConditionalOnBean(Array(classOf[GitProperties]))
    @ConditionalOnClass(Array(classOf[GitInfo]))
    def gitInfo(gitProps: GitProperties): GitInfo = {
      new GitInfo(
        gitProps.getBranch,
        gitProps.getCommitId,
        gitProps.get("commit.user.name"),
        gitProps.get("commit.message.full"),
        new Date(gitProps.getCommitTime.toEpochMilli)
      )
    }
  }

}
