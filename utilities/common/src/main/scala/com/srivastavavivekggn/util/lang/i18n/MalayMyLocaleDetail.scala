package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * ms-MY locale detail
  */
class MalayMyLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("ms-MY")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "Januari", "Februari", "Mac", "April", "Mei", "Jun", "Julai", "Ogos", "September", "Oktober", "November", "Disember"
  )

  override val monthsShort: List[String] = List(
      "Jan", "Feb", "Mac", "Apr", "Mei", "Jun", "Jul", "Ogs", "Sep", "Okt", "Nov", "Dis"
  )

  override val weekdays: List[String] = List(
      "Ahad", "Isnin", "Selasa", "Rabu", "Khamis", "Jumaat", "Sabtu"
  )

  override val weekdaysShort: List[String] = List(
      "Ahd", "Isn", "Sel", "Rab", "Kha", "Jum", "Sab"
  )

  override val amPm: List[String] = List(
      "pagi", "tengahari"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // HH.mm, e.g. 14.15 
        DateTimeFormats.LT -> s"{{$IN|date:HH}}.{{$IN|date:mm}}",
        
        // HH.mm.ss, e.g. 14.15.44 
        DateTimeFormats.LTS -> s"{{$IN|date:HH}}.{{$IN|date:mm}}.{{$IN|date:ss}}",
        
        // DD/MM/YYYY, e.g. 03/01/2021 
        DateTimeFormats.L -> s"$DAY2/$MONTH2/$YEAR",
        
        // DD/MM/YYYY, e.g. 3/1/2021 
        DateTimeFormats.l -> s"$DAY/$MONTH/$YEAR",
        
        // D MMMM YYYY, e.g. 3 Januari 2021 
        DateTimeFormats.LL -> s"$DAY $MONTH_LONG $YEAR",
        
        // D MMM YYYY, e.g. 3 Jan 2021 
        DateTimeFormats.ll -> s"$DAY $MONTH_SHORT $YEAR",
        
        // D MMMM YYYY [pukul] HH.mm, e.g. 3 Januari 2021 pukul 14.15 
        DateTimeFormats.LLL -> s"$DAY $MONTH_LONG $YEAR pukul {{$IN|date:HH}}.{{$IN|date:mm}}",
        
        // D MMM YYYY [pukul] HH.mm, e.g. 3 Jan 2021 pukul 14.15 
        DateTimeFormats.lll -> s"$DAY $MONTH_SHORT $YEAR pukul {{$IN|date:HH}}.{{$IN|date:mm}}",
        
        // dddd, D MMMM YYYY [pukul] HH.mm, e.g. Ahad, 3 Januari 2021 pukul 14.15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY $MONTH_LONG $YEAR pukul {{$IN|date:HH}}.{{$IN|date:mm}}",
        
        // ddd, D MMM YYYY [pukul] HH.mm, e.g. Ahd, 3 Jan 2021 pukul 14.15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY $MONTH_SHORT $YEAR pukul {{$IN|date:HH}}.{{$IN|date:mm}}"
  )
}

