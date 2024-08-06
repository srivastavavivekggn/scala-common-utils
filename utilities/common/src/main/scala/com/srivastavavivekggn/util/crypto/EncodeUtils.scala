package com.srivastavavivekggn.scala.util.crypto

import java.net.{URLDecoder, URLEncoder}

import com.google.common.io.BaseEncoding

/**
  * Utility for encoding strings
  */
object EncodeUtils {

  final val UTF_8 = "UTF-8"

  def encodeBase64(str: String): String = {
    BaseEncoding.base64().encode(str.getBytes)
  }

  def encodeBase64(str: Array[Byte]): String = {
    BaseEncoding.base64().encode(str)
  }

  def encodeBase32(str: Array[Byte]): String = {
    BaseEncoding.base32().encode(str)
  }

  def encodeBase16(str: Array[Byte]): String = {
    BaseEncoding.base16().encode(str)
  }

  def decodeBase64(str: String): String = {
    new String(decode(str))
  }

  def decode(str: String): Array[Byte] = {
    BaseEncoding.base64().decode(str)
  }

  def urlEncode(name: String, charset: String = UTF_8): String = {
    URLEncoder.encode(name, charset)
  }

  def urlDecode(name: String, charset: String = UTF_8): String = {
    URLDecoder.decode(name, charset)
  }

}
