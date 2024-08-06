package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * hi-IN locale detail
  */
class HindiInLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("hi-IN")) {

  override val numbers: List[String] = List(
      "०", "१", "२", "३", "४", "५", "६", "७", "८", "९"
  )

  override val months: List[String] = List(
      "जनवरी", "फ़रवरी", "मार्च", "अप्रैल", "मई", "जून", "जुलाई", "अगस्त", "सितम्बर", "अक्टूबर", "नवम्बर", "दिसम्बर"
  )

  override val monthsShort: List[String] = List(
      "जन.", "फ़र.", "मार्च", "अप्रै.", "मई", "जून", "जुल.", "अग.", "सित.", "अक्टू.", "नव.", "दिस."
  )

  override val weekdays: List[String] = List(
      "रविवार", "सोमवार", "मंगलवार", "बुधवार", "गुरूवार", "शुक्रवार", "शनिवार"
  )

  override val weekdaysShort: List[String] = List(
      "रवि", "सोम", "मंगल", "बुध", "गुरू", "शुक्र", "शनि"
  )

  override val amPm: List[String] = List(
      "रात", "दोपहर"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // A h:mm बजे, e.g. दोपहर २:१५ बजे 
        DateTimeFormats.LT -> s"$AM_PM $H_MM बजे",
        
        // A h:mm:ss बजे, e.g. दोपहर २:१५:४४ बजे 
        DateTimeFormats.LTS -> s"$AM_PM $H_MM_SS बजे",
        
        // DD/MM/YYYY, e.g. ०३/०१/२०२१ 
        DateTimeFormats.L -> s"$DAY2/$MONTH2/$YEAR",
        
        // DD/MM/YYYY, e.g. ३/१/२०२१ 
        DateTimeFormats.l -> s"$DAY/$MONTH/$YEAR",
        
        // D MMMM YYYY, e.g. ३ जनवरी २०२१ 
        DateTimeFormats.LL -> s"$DAY $MONTH_LONG $YEAR",
        
        // D MMM YYYY, e.g. ३ जन. २०२१ 
        DateTimeFormats.ll -> s"$DAY $MONTH_SHORT $YEAR",
        
        // D MMMM YYYY, A h:mm बजे, e.g. ३ जनवरी २०२१, दोपहर २:१५ बजे 
        DateTimeFormats.LLL -> s"$DAY $MONTH_LONG $YEAR, $AM_PM $H_MM बजे",
        
        // D MMM YYYY, A h:mm बजे, e.g. ३ जन. २०२१, दोपहर २:१५ बजे 
        DateTimeFormats.lll -> s"$DAY $MONTH_SHORT $YEAR, $AM_PM $H_MM बजे",
        
        // dddd, D MMMM YYYY, A h:mm बजे, e.g. रविवार, ३ जनवरी २०२१, दोपहर २:१५ बजे 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY $MONTH_LONG $YEAR, $AM_PM $H_MM बजे",
        
        // ddd, D MMM YYYY, A h:mm बजे, e.g. रवि, ३ जन. २०२१, दोपहर २:१५ बजे 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY $MONTH_SHORT $YEAR, $AM_PM $H_MM बजे"
  )
}

