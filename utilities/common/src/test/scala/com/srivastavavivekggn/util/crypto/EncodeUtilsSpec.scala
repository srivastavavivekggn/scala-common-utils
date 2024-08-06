package com.srivastavavivekggn.scala.util.crypto

import com.srivastavavivekggn.scala.util.BaseUtilSpec

class EncodeUtilsSpec extends BaseUtilSpec {

  behavior of "EncodeUtils"

  it must "encode a URL" in {

    var url = "http://www.xxxxx.com?c=1234$"

    val encoded = EncodeUtils.urlEncode(url, "UTF-8")
    val encoded2 = EncodeUtils.urlEncode(url)

    assert(encoded != null)
    assert(encoded == "http%3A%2F%2Fwww.xxxxx.com%3Fc%3D1234%24")
    assert(encoded == encoded2)
  }

  it must "decode a URL" in {

    val url = "http%3A%2F%2Fwww.xxxxx.com%3Fc%3D1234%24"

    val decoded = EncodeUtils.urlDecode(url, "UTF-8")
    val decoded2 = EncodeUtils.urlDecode(url)
    assert(decoded != null)
    assert(decoded == "http://www.xxxxx.com?c=1234$")
    assert(decoded == decoded2)
  }



}
