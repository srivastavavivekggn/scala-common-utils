package com.srivastavavivekggn.scala.util.domain

trait BaseModel[ID <: java.io.Serializable] {

  def getId: ID

}
