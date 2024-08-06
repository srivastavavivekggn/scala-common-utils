package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * de-DE locale detail
  */
class GermanDeLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("de-DE")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "Januar", "Februar", "März", "April", "Mai", "Juni", "Juli", "August", "September", "Oktober", "November", "Dezember"
  )

  override val monthsShort: List[String] = List(
      "Jan.", "Feb.", "März", "Apr.", "Mai", "Juni", "Juli", "Aug.", "Sep.", "Okt.", "Nov.", "Dez."
  )

  override val weekdays: List[String] = List(
      "Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"
  )

  override val weekdaysShort: List[String] = List(
      "So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.", "Sa."
  )

  override val amPm: List[String] = List(
      "AM", "PM"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // HH:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$HH24_MM",
        
        // HH:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$HH24_MM_SS",
        
        // DD.MM.YYYY, e.g. 03.01.2021 
        DateTimeFormats.L -> s"$DAY2.$MONTH2.$YEAR",
        
        // DD.MM.YYYY, e.g. 3.1.2021 
        DateTimeFormats.l -> s"$DAY.$MONTH.$YEAR",
        
        // D. MMMM YYYY, e.g. 3. Januar 2021 
        DateTimeFormats.LL -> s"$DAY. $MONTH_LONG $YEAR",
        
        // D. MMM YYYY, e.g. 3. Jan. 2021 
        DateTimeFormats.ll -> s"$DAY. $MONTH_SHORT $YEAR",
        
        // D. MMMM YYYY HH:mm, e.g. 3. Januar 2021 14:15 
        DateTimeFormats.LLL -> s"$DAY. $MONTH_LONG $YEAR $HH24_MM",
        
        // D. MMM YYYY HH:mm, e.g. 3. Jan. 2021 14:15 
        DateTimeFormats.lll -> s"$DAY. $MONTH_SHORT $YEAR $HH24_MM",
        
        // dddd, D. MMMM YYYY HH:mm, e.g. Sonntag, 3. Januar 2021 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY. $MONTH_LONG $YEAR $HH24_MM",
        
        // ddd, D. MMM YYYY HH:mm, e.g. So., 3. Jan. 2021 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY. $MONTH_SHORT $YEAR $HH24_MM"
  )
}

