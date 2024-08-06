package com.srivastavavivekggn.scala.util.crypto.aes

/**
  * Created by JohnDeverna on 1/2/15.
  */
sealed trait KeyEncoding

case object BASE64 extends KeyEncoding
case object BASE32 extends KeyEncoding
case object HEX    extends KeyEncoding
