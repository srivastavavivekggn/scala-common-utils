package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * es-US locale detail
  */
class SpanishUsLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("es-US")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
  )

  override val monthsShort: List[String] = List(
      "ene.", "feb.", "mar.", "abr.", "may.", "jun.", "jul.", "ago.", "sep.", "oct.", "nov.", "dic."
  )

  override val weekdays: List[String] = List(
      "domingo", "lunes", "martes", "miércoles", "jueves", "viernes", "sábado"
  )

  override val weekdaysShort: List[String] = List(
      "dom.", "lun.", "mar.", "mié.", "jue.", "vie.", "sáb."
  )

  override val amPm: List[String] = List(
      "AM", "PM"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // h:mm A, e.g. 2:15 PM 
        DateTimeFormats.LT -> s"$H_MM $AM_PM",
        
        // h:mm:ss A, e.g. 2:15:44 PM 
        DateTimeFormats.LTS -> s"$H_MM_SS $AM_PM",
        
        // MM/DD/YYYY, e.g. 01/03/2021 
        DateTimeFormats.L -> s"$MONTH2/$DAY2/$YEAR",
        
        // MM/DD/YYYY, e.g. 1/3/2021 
        DateTimeFormats.l -> s"$MONTH/$DAY/$YEAR",
        
        // D [de] MMMM [de] YYYY, e.g. 3 de enero de 2021 
        DateTimeFormats.LL -> s"$DAY de $MONTH_LONG de $YEAR",
        
        // D [de] MMM [de] YYYY, e.g. 3 de ene. de 2021 
        DateTimeFormats.ll -> s"$DAY de $MONTH_SHORT de $YEAR",
        
        // D [de] MMMM [de] YYYY h:mm A, e.g. 3 de enero de 2021 2:15 PM 
        DateTimeFormats.LLL -> s"$DAY de $MONTH_LONG de $YEAR $H_MM $AM_PM",
        
        // D [de] MMM [de] YYYY h:mm A, e.g. 3 de ene. de 2021 2:15 PM 
        DateTimeFormats.lll -> s"$DAY de $MONTH_SHORT de $YEAR $H_MM $AM_PM",
        
        // dddd, D [de] MMMM [de] YYYY h:mm A, e.g. domingo, 3 de enero de 2021 2:15 PM 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY de $MONTH_LONG de $YEAR $H_MM $AM_PM",
        
        // ddd, D [de] MMM [de] YYYY h:mm A, e.g. dom., 3 de ene. de 2021 2:15 PM 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY de $MONTH_SHORT de $YEAR $H_MM $AM_PM"
  )
}

