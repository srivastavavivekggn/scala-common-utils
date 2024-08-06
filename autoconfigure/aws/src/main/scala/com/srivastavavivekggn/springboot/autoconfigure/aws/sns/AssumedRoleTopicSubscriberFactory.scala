package com.srivastavavivekggn.springboot.autoconfigure.aws.sns

import software.amazon.awssdk.awscore.defaultsmode.DefaultsMode
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sns.SnsClient
import software.amazon.awssdk.services.sts.StsClient
import software.amazon.awssdk.services.sts.auth.StsAssumeRoleCredentialsProvider
import software.amazon.awssdk.services.sts.model.AssumeRoleRequest

case class AssumedRoleTopicSubscriberFactory(stsClient: StsClient,
                                             region: Region) {


  def createTopicSubscriber(roleArn: String,
                            roleSessionName: String): AssumedRoleTopicSubscriber = {

    val creds = StsAssumeRoleCredentialsProvider.builder()
      .refreshRequest(
        AssumeRoleRequest.builder().roleArn(roleArn).roleSessionName(roleSessionName).build()
      )
      .stsClient(stsClient)
      .build()

    val snsClient = SnsClient.builder()
      .defaultsMode(DefaultsMode.STANDARD)
      .credentialsProvider(creds)
      .region(region)
      .build()

    AssumedRoleTopicSubscriber(snsClient)
  }
}
