package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * fr-CA locale detail
  */
class FrenchCaLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("fr-CA")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"
  )

  override val monthsShort: List[String] = List(
      "janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc."
  )

  override val weekdays: List[String] = List(
      "dimanche", "lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi"
  )

  override val weekdaysShort: List[String] = List(
      "dim.", "lun.", "mar.", "mer.", "jeu.", "ven.", "sam."
  )

  override val amPm: List[String] = List(
      "AM", "PM"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // HH:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$HH24_MM",
        
        // HH:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$HH24_MM_SS",
        
        // YYYY-MM-DD, e.g. 2021-01-03 
        DateTimeFormats.L -> s"$YEAR-$MONTH2-$DAY2",
        
        // YYYY-MM-DD, e.g. 2021-1-3 
        DateTimeFormats.l -> s"$YEAR-$MONTH-$DAY",
        
        // D MMMM YYYY, e.g. 3 janvier 2021 
        DateTimeFormats.LL -> s"$DAY $MONTH_LONG $YEAR",
        
        // D MMM YYYY, e.g. 3 janv. 2021 
        DateTimeFormats.ll -> s"$DAY $MONTH_SHORT $YEAR",
        
        // D MMMM YYYY HH:mm, e.g. 3 janvier 2021 14:15 
        DateTimeFormats.LLL -> s"$DAY $MONTH_LONG $YEAR $HH24_MM",
        
        // D MMM YYYY HH:mm, e.g. 3 janv. 2021 14:15 
        DateTimeFormats.lll -> s"$DAY $MONTH_SHORT $YEAR $HH24_MM",
        
        // dddd D MMMM YYYY HH:mm, e.g. dimanche 3 janvier 2021 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG $DAY $MONTH_LONG $YEAR $HH24_MM",
        
        // ddd D MMM YYYY HH:mm, e.g. dim. 3 janv. 2021 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT $DAY $MONTH_SHORT $YEAR $HH24_MM"
  )
}

