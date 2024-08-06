package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * ko-KR locale detail
  */
class KoreanKrLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("ko-KR")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
  )

  override val monthsShort: List[String] = List(
      "1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"
  )

  override val weekdays: List[String] = List(
      "일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"
  )

  override val weekdaysShort: List[String] = List(
      "일", "월", "화", "수", "목", "금", "토"
  )

  override val amPm: List[String] = List(
      "오전", "오후"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // A h:mm, e.g. 오후 2:15 
        DateTimeFormats.LT -> s"$AM_PM $H_MM",
        
        // A h:mm:ss, e.g. 오후 2:15:44 
        DateTimeFormats.LTS -> s"$AM_PM $H_MM_SS",
        
        // YYYY.MM.DD., e.g. 2021.01.03. 
        DateTimeFormats.L -> s"$YEAR.$MONTH2.$DAY2.",
        
        // YYYY.MM.DD., e.g. 2021.01.03. 
        DateTimeFormats.l -> s"$YEAR.$MONTH2.$DAY2.",
        
        // YYYY년 MMMM D일, e.g. 2021년 1월 3일 
        DateTimeFormats.LL -> s"${YEAR}년 $MONTH_LONG ${DAY}일",
        
        // YYYY년 MMMM D일, e.g. 2021년 1월 3일 
        DateTimeFormats.ll -> s"${YEAR}년 $MONTH_LONG ${DAY}일",
        
        // YYYY년 MMMM D일 A h:mm, e.g. 2021년 1월 3일 오후 2:15 
        DateTimeFormats.LLL -> s"${YEAR}년 $MONTH_LONG ${DAY}일 $AM_PM $H_MM",
        
        // YYYY년 MMMM D일 A h:mm, e.g. 2021년 1월 3일 오후 2:15 
        DateTimeFormats.lll -> s"${YEAR}년 $MONTH_LONG ${DAY}일 $AM_PM $H_MM",
        
        // YYYY년 MMMM D일 dddd A h:mm, e.g. 2021년 1월 3일 일요일 오후 2:15 
        DateTimeFormats.LLLL -> s"${YEAR}년 $MONTH_LONG ${DAY}일 $WEEKDAY_LONG $AM_PM $H_MM",
        
        // YYYY년 MMMM D일 dddd A h:mm, e.g. 2021년 1월 3일 일요일 오후 2:15 
        DateTimeFormats.llll -> s"${YEAR}년 $MONTH_LONG ${DAY}일 $WEEKDAY_LONG $AM_PM $H_MM"
  )
}

