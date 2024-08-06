package com.srivastavavivekggn.springboot.autoconfigure.aws

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.{ConditionalOnClass, ConditionalOnMissingBean, ConditionalOnProperty}
import org.springframework.context.annotation.{Bean, Configuration}
import software.amazon.awssdk.auth.credentials.{AwsBasicCredentials, AwsCredentialsProvider, StaticCredentialsProvider}
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.regions.providers.AwsRegionProvider

@Configuration
@ConditionalOnClass(Array(classOf[AwsCredentialsProvider]))
@ConditionalOnMissingBean(Array(classOf[AwsCredentialsProvider]))
@ConditionalOnProperty(
  prefix = "aws.credentials",
  name = Array("aws_access_key_id", "aws_secret_access_key"),
  matchIfMissing = false
)
class AwsCredentialsAutoConfiguration {

  @Value("${aws.credentials.aws_access_key_id}")
  val accessKey: String = ""

  @Value("${aws.credentials.aws_secret_access_key}")
  val secretKey: String = ""

  /**
    * Create the aws credentials bean
    *
    * @return the credentials bean
    */
  @Bean
  def awsCredentialsProvider: AwsCredentialsProvider = {
    StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
  }

  /**
    * Define the region provider
    *
    * @param awsRegion the aws region property value
    * @return the region provider
    */
  @Bean
  @ConditionalOnClass(Array(classOf[AwsRegionProvider]))
  def awsRegionProvider(@Value("${aws.sqs.region:${aws.region:us-east-1}}") awsRegion: String): AwsRegionProvider = {
    new AwsRegionProvider {
      override def getRegion: Region = Region.of(awsRegion)
    }
  }
}
