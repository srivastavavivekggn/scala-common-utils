package com.srivastavavivekggn.springboot.autoconfigure.feign

import feign.Feign
import org.springframework.boot.autoconfigure.condition.{ConditionalOnClass, ConditionalOnProperty}
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration


/**
  * Configuration for Open-Feign.
  */
@Configuration
@ConditionalOnClass(Array(classOf[Feign]))
class FeignAutoConfiguration {

}

object FeignAutoConfiguration {

  @ConditionalOnProperty(prefix = "feign", name = Array("base-package"), matchIfMissing = false)
  @EnableFeignClients(basePackages = Array("${feign.base-package}"))
  class FeignClientsAutoConfiguration {}

}
