package com.srivastavavivekggn.scala.util.placeholder

import java.time.LocalDate
import java.util.Locale

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.placeholder.PlaceholderUtils.ReplacementMode
import com.srivastavavivekggn.scala.util.placeholder.context.{CompositePlaceholderContextProvider, LegacyEncodingPlaceholderContextProvider, QuotedStringPlaceholderContextProvider, StaticPlaceholderContextProvider}
import com.srivastavavivekggn.scala.util.placeholder.format.TranslationValueFormatter
import org.springframework.context.support.StaticMessageSource

class PlaceholderUtilsSpec extends BaseUtilSpec {

  val simpleReplacementString = "{{greeting |x   }}, {{  name}}! How's it {{verbs.primary}}? Click here {{ link|e }} before {{ startDate|date:YYYY }}"

  val nestedReplacementString = "Hi! My {{ {{name|x}} | translate }}"

  // shared re-usable message source
  val messageSource = {
    val ms = new StaticMessageSource()
    ms.addMessage("March", Locale.ENGLISH, "March")
    ms.addMessage("March", Locale.GERMANY, "März")
    ms.addMessage("March", Locale.SIMPLIFIED_CHINESE, "三月")
    ms.addMessage("March", Locale.FRANCE, "mars")
    ms.addMessage("March", Locale.JAPANESE, "3月")

    ms.addMessage("month.and.year", Locale.ENGLISH, "It is {0} of {1}")
    ms.addMessage("month.and.year", Locale.GERMANY, "ein {1} de {0}")

    ms
  }

  val staticDateContext = StaticPlaceholderContextProvider(Map(
    "startDate" -> Option(LocalDate.of(2020, 3, 22)),
    "endDate" -> Option(LocalDate.of(2021, 4, 22))
  ))

  val compositeContext = CompositePlaceholderContextProvider(
    staticDateContext,
    new QuotedStringPlaceholderContextProvider
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  behavior of "PlaceholderUtils.getPlaceholders"

  it should "return all placeholders (simple list)" in {
    val result = PlaceholderUtils.getPlaceholders(simpleReplacementString)

    val keys = result.map(_.key)

    assertResult(List("greeting", "name", "verbs.primary", "link", "startDate"))(keys)

    assert(result.exists(r => r.key.equals("link") && r.format.head.formatType.equals("e")))
    assert(result.exists(r => r.key.equals("startDate") && r.format.head.formatArgs.head.equals("YYYY")))
  }

  it should "return all placeholders (nested placeholders)" in {
    val result = PlaceholderUtils.getPlaceholders(nestedReplacementString)

    val keys = result.map(_.key)

    assertResult(List("name"))(keys)
  }


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  behavior of "PlaceholderUtils.replaceAllPlaceholders"

  it should "resolve all placeholder values" in {
    val result = PlaceholderUtils.replaceAllPlaceholders(simpleReplacementString, Map(
      "greeting" -> "Hi",
      "name" -> "Bob",
      "verbs.primary" -> Option("hanging"),
      "link" -> "https://www.google.com?a=1&b=2",
      "startDate" -> LocalDate.of(2020, 3, 15)
    ))

    assertResult("Hi, Bob! How's it hanging? Click here https%3A%2F%2Fwww.google.com%3Fa%3D1%26b%3D2 before 2020")(result)
  }

  it should "handle placeholders with no replacement value" in {
    val result1 = PlaceholderUtils.replaceAllPlaceholders(simpleReplacementString, Map(
      "greeting" -> "Hi",
      "name" -> "Bob"
    ), replacementMode = ReplacementMode.LEAVE_PLACEHOLDERS)

    val result2 = PlaceholderUtils.replaceAllPlaceholders(simpleReplacementString, Map(
      "greeting" -> "Hi",
      "name" -> "Bob"
    ), replacementMode = ReplacementMode.STRIP_PLACEHOLDERS)

    val result3 = PlaceholderUtils.replaceAllPlaceholders(simpleReplacementString, Map(
      "greeting" -> "Hi",
      "name" -> "Bob"
    ), replacementMode = ReplacementMode.DROP_BRACES)

    assertResult("Hi, Bob! How's it {{verbs.primary}}? Click here {{ link|e }} before {{ startDate|date:YYYY }}")(result1)
    assertResult("Hi, Bob! How's it ? Click here  before ")(result2)
    assertResult("Hi, Bob! How's it verbs.primary? Click here link before startDate")(result3)
  }

  it should "replace placeholders introduced during previous replacement iterations" in {
    val result = PlaceholderUtils.replaceAllPlaceholders("Hi {{name }}!", Map(
      "name" -> "{{name2}}",
      "name2" -> "{{name3|xyz}}",
      "name3" -> "Tim"
    ))

    assertResult("Hi Tim!")(result)
  }

  it should "utilize appropriate locale when formatting" in {

    val str = "{{startDate|date:MMMM|translate}}"

    def forLocale(l: Locale): String = PlaceholderUtils.replaceAllPlaceholders(
      in = str,
      replacementCtx = staticDateContext.map,
      formatters = List(TranslationValueFormatter(messageSource)),
      locale = l
    )

    val english = forLocale(Locale.ENGLISH)
    val german = forLocale(Locale.GERMANY)
    val chinese = forLocale(Locale.SIMPLIFIED_CHINESE)
    val french = forLocale(Locale.FRANCE)

    assertResult("March")(english)
    assertResult("März")(german)
    assertResult("三月")(chinese)
    assertResult("mars")(french)
  }


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  behavior of "PlaceholderUtils.replaceAll"

  it should "use the a provided replacement context" in {

    val str = "{{startDate|date:MMMM|translate}} {{startDate|date:YYYY}}"

    def forLocale(l: Locale): String = PlaceholderUtils.replaceAll(
      in = str, replacementCtx = staticDateContext, formatters = List(TranslationValueFormatter(messageSource)), locale = l
    )

    val english = forLocale(Locale.ENGLISH)
    val german = forLocale(Locale.GERMANY)
    val chinese = forLocale(Locale.SIMPLIFIED_CHINESE)
    val french = forLocale(Locale.FRANCE)

    assertResult("March 2020")(english)
    assertResult("März 2020")(german)
    assertResult("三月 2020")(chinese)
    assertResult("mars 2020")(french)
  }


  it should "pass the formatType as an argument when formatting by value" in {
    val str = "{{startDate|YYYY}}"

    def forLocale(l: Locale): String = PlaceholderUtils.replaceAll(
      in = str, replacementCtx = staticDateContext, formatters = List(TranslationValueFormatter(messageSource))
    )

    val english = forLocale(Locale.ENGLISH)
    assertResult("2020")(english)
  }


  it should "handle nested placeholders (1 level deep)" in {

    val s = "{{ 'month.and.year'|translate:{{startDate|date:MMMM|translate}}:{{startDate|date:YYYY}} }}"

    def forLocale(l: Locale): String = PlaceholderUtils.replaceAll(
      in = s,
      replacementCtx = compositeContext,
      formatters = List(TranslationValueFormatter(messageSource)),
      locale = l
    )

    val english = forLocale(Locale.ENGLISH)
    val german = forLocale(Locale.GERMANY)

    assertResult("It is March of 2020")(english)
    assertResult("ein 2020 de März")(german)
  }

  it should "handle nested placeholders (arbitrarily deep)" in {

    val s = "{{ {{ {{ {{ a }} }} }} }}"

    val placeholders = PlaceholderUtils.getPlaceholders(s)
    val result = PlaceholderUtils.replaceAll(s, StaticPlaceholderContextProvider(
      Map("a" -> "b",
        "b" -> "c",
        "c" -> "d",
        "d" -> "e"
      )
    ))

    assert(placeholders.size == 1 && placeholders.head.key.equals("a"))
    assertResult("e")(result)
  }


  it should "utilize multiple replacement contexts" in {
    val str = "{{startDate|date:MMMM}} {{startDate|date:YYYY}} to {{endDate|date:MMMM}} {{endDate|date:YYYY}}"

    val result = PlaceholderUtils.replaceAll(
      str,
      compositeContext
    )

    assertResult("March 2020 to April 2021")(result)
  }

  it should "handle legacy placeholder resolution" in {
    val legacyString = "{{greeting |x   }}, {{  name}}! How's it {{verbs.primary}}? Click here {{ e:link }} before {{ startDate|date:YYYY }}"

    val context = CompositePlaceholderContextProvider(
      StaticPlaceholderContextProvider(Map(
        "greeting" -> "Hi",
        "name" -> "Bob",
        "verbs.primary" -> Option("hanging"),
        "link" -> "https://www.google.com?a=1&b=2",
        "startDate" -> LocalDate.of(2020, 3, 15)
      )),
      new LegacyEncodingPlaceholderContextProvider
    )

    val result = PlaceholderUtils.replaceAll(legacyString, context)

    assertResult("Hi, Bob! How's it hanging? Click here https%3A%2F%2Fwww.google.com%3Fa%3D1%26b%3D2 before 2020")(result)
  }

  it should "replace alias monthName" in {

    val str = "{{startDate|monthName}}"

    def forLocale(l: Locale): String = PlaceholderUtils.replaceAll(
      in = str, replacementCtx = staticDateContext, formatters = List(TranslationValueFormatter(messageSource)), locale = l
    )

    val english = forLocale(Locale.ENGLISH)
    val german = forLocale(Locale.GERMANY)
    val chinese = forLocale(Locale.SIMPLIFIED_CHINESE)
    val french = forLocale(Locale.FRANCE)

    assertResult("March")(english)
    assertResult("März")(german)
    assertResult("三月")(chinese)
    assertResult("mars")(french)
  }

}
