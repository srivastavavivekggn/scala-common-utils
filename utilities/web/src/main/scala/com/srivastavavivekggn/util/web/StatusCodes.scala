package com.srivastavavivekggn.scala.util.web

/**
  * StatusCodes object - this allows for usage as annotation parameter values
  */
object StatusCodes extends StatusCodes

/**
  * The fixed set of Http status codes
  */
sealed trait StatusCodes {
  final val OK = 200

  final val NOT_FOUND = 404

  final val NO_CONTENT = 204

  final val BAD_REQUEST = 400

  final val INTERNAL_SERVER_ERROR = 500
}
