package com.srivastavavivekggn.springboot.autoconfigure.web.tomcat

import javax.servlet.http.HttpServletRequest

trait TomcatAccessDelegate {

  def pattern: Char

  def name: String

  def process(req: HttpServletRequest): String

}
