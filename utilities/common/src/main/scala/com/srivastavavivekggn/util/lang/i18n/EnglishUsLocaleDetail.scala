package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * en-US locale detail
  */
class EnglishUsLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("en-US")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
  )

  override val monthsShort: List[String] = List(
      "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
  )

  override val weekdays: List[String] = List(
      "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
  )

  override val weekdaysShort: List[String] = List(
      "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
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
        
        // MMMM D, YYYY, e.g. January 3, 2021 
        DateTimeFormats.LL -> s"$MONTH_LONG $DAY, $YEAR",
        
        // MMM D, YYYY, e.g. Jan 3, 2021 
        DateTimeFormats.ll -> s"$MONTH_SHORT $DAY, $YEAR",
        
        // MMMM D, YYYY h:mm A, e.g. January 3, 2021 2:15 PM 
        DateTimeFormats.LLL -> s"$MONTH_LONG $DAY, $YEAR $H_MM $AM_PM",
        
        // MMM D, YYYY h:mm A, e.g. Jan 3, 2021 2:15 PM 
        DateTimeFormats.lll -> s"$MONTH_SHORT $DAY, $YEAR $H_MM $AM_PM",
        
        // dddd, MMMM D, YYYY h:mm A, e.g. Sunday, January 3, 2021 2:15 PM 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $MONTH_LONG $DAY, $YEAR $H_MM $AM_PM",
        
        // ddd, MMM D, YYYY h:mm A, e.g. Sun, Jan 3, 2021 2:15 PM 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $MONTH_SHORT $DAY, $YEAR $H_MM $AM_PM"
  )
}

