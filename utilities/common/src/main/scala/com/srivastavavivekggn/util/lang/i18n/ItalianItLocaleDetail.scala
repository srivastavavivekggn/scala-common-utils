package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * it-IT locale detail
  */
class ItalianItLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("it-IT")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "gennaio", "febbraio", "marzo", "aprile", "maggio", "giugno", "luglio", "agosto", "settembre", "ottobre", "novembre", "dicembre"
  )

  override val monthsShort: List[String] = List(
      "gen", "feb", "mar", "apr", "mag", "giu", "lug", "ago", "set", "ott", "nov", "dic"
  )

  override val weekdays: List[String] = List(
      "domenica", "lunedì", "martedì", "mercoledì", "giovedì", "venerdì", "sabato"
  )

  override val weekdaysShort: List[String] = List(
      "dom", "lun", "mar", "mer", "gio", "ven", "sab"
  )

  override val amPm: List[String] = List(
      "AM", "PM"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // HH:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$HH24_MM",
        
        // HH:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$HH24_MM_SS",
        
        // DD/MM/YYYY, e.g. 03/01/2021 
        DateTimeFormats.L -> s"$DAY2/$MONTH2/$YEAR",
        
        // DD/MM/YYYY, e.g. 3/1/2021 
        DateTimeFormats.l -> s"$DAY/$MONTH/$YEAR",
        
        // D MMMM YYYY, e.g. 3 gennaio 2021 
        DateTimeFormats.LL -> s"$DAY $MONTH_LONG $YEAR",
        
        // D MMM YYYY, e.g. 3 gen 2021 
        DateTimeFormats.ll -> s"$DAY $MONTH_SHORT $YEAR",
        
        // D MMMM YYYY HH:mm, e.g. 3 gennaio 2021 14:15 
        DateTimeFormats.LLL -> s"$DAY $MONTH_LONG $YEAR $HH24_MM",
        
        // D MMM YYYY HH:mm, e.g. 3 gen 2021 14:15 
        DateTimeFormats.lll -> s"$DAY $MONTH_SHORT $YEAR $HH24_MM",
        
        // dddd D MMMM YYYY HH:mm, e.g. domenica 3 gennaio 2021 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG $DAY $MONTH_LONG $YEAR $HH24_MM",
        
        // ddd D MMM YYYY HH:mm, e.g. dom 3 gen 2021 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT $DAY $MONTH_SHORT $YEAR $HH24_MM"
  )
}

