package com.srivastavavivekggn.springboot.autoconfigure.aws.sts

import com.srivastavavivekggn.springboot.autoconfigure.aws.AwsCredentialsAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.{ConditionalOnBean, ConditionalOnClass}
import org.springframework.context.annotation.{Bean, Configuration}
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
import software.amazon.awssdk.regions.providers.AwsRegionProvider
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sts.StsClient

@Configuration
@AutoConfigureAfter(Array(classOf[AwsCredentialsAutoConfiguration]))
@ConditionalOnClass(Array(classOf[SnsClient]))
class AwsStsAutoConfiguration {

  /**
    * STS token service
    *
    * @param awsCredentials the aws credentials
    * @param region         the region
    * @return the STS client
    */
  @Bean
  @ConditionalOnBean(Array(classOf[AwsCredentialsProvider]))
  def awsSecurityTokenService(awsCredentials: AwsCredentialsProvider,
                              region: AwsRegionProvider): StsClient = {

    val service = StsClient.builder()
      .defaultsMode(DefaultsMode.STANDARD)
      .credentialsProvider(awsCredentials)
      .region(region.getRegion)
      .build()

    service
  }
}
