package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * es-ES locale detail
  */
class SpanishEsLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("es-ES")) {

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
    
        // H:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$H24_MM",
        
        // H:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$H24_MM_SS",
        
        // DD/MM/YYYY, e.g. 03/01/2021 
        DateTimeFormats.L -> s"$DAY2/$MONTH2/$YEAR",
        
        // DD/MM/YYYY, e.g. 3/1/2021 
        DateTimeFormats.l -> s"$DAY/$MONTH/$YEAR",
        
        // D [de] MMMM [de] YYYY, e.g. 3 de enero de 2021 
        DateTimeFormats.LL -> s"$DAY de $MONTH_LONG de $YEAR",
        
        // D [de] MMM [de] YYYY, e.g. 3 de ene. de 2021 
        DateTimeFormats.ll -> s"$DAY de $MONTH_SHORT de $YEAR",
        
        // D [de] MMMM [de] YYYY H:mm, e.g. 3 de enero de 2021 14:15 
        DateTimeFormats.LLL -> s"$DAY de $MONTH_LONG de $YEAR $H24_MM",
        
        // D [de] MMM [de] YYYY H:mm, e.g. 3 de ene. de 2021 14:15 
        DateTimeFormats.lll -> s"$DAY de $MONTH_SHORT de $YEAR $H24_MM",
        
        // dddd, D [de] MMMM [de] YYYY H:mm, e.g. domingo, 3 de enero de 2021 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY de $MONTH_LONG de $YEAR $H24_MM",
        
        // ddd, D [de] MMM [de] YYYY H:mm, e.g. dom., 3 de ene. de 2021 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY de $MONTH_SHORT de $YEAR $H24_MM"
  )
}

