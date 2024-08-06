package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * zh-CN locale detail
  */
class ChineseCnLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("zh-CN")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"
  )

  override val monthsShort: List[String] = List(
      "1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"
  )

  override val weekdays: List[String] = List(
      "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
  )

  override val weekdaysShort: List[String] = List(
      "周日", "周一", "周二", "周三", "周四", "周五", "周六"
  )

  override val amPm: List[String] = List(
      "凌晨", "下午"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // HH:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$HH24_MM",
        
        // HH:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$HH24_MM_SS",
        
        // YYYY/MM/DD, e.g. 2021/01/03 
        DateTimeFormats.L -> s"$YEAR/$MONTH2/$DAY2",
        
        // YYYY/M/D, e.g. 2021/1/3 
        DateTimeFormats.l -> s"$YEAR/$MONTH/$DAY",
        
        // YYYY年M月D日, e.g. 2021年1月3日 
        DateTimeFormats.LL -> s"${YEAR}年${MONTH}月${DAY}日",
        
        // YYYY年M月D日, e.g. 2021年1月3日 
        DateTimeFormats.ll -> s"${YEAR}年${MONTH}月${DAY}日",
        
        // YYYY年M月D日Ah点mm分, e.g. 2021年1月3日下午2点15分 
        DateTimeFormats.LLL -> s"${YEAR}年${MONTH}月${DAY}日$AM_PM{{$IN|date:h}}点{{$IN|date:mm}}分",
        
        // YYYY年M月D日 HH:mm, e.g. 2021年1月3日 14:15 
        DateTimeFormats.lll -> s"${YEAR}年${MONTH}月${DAY}日 $HH24_MM",
        
        // YYYY年M月D日ddddAh点mm分, e.g. 2021年1月3日星期日下午2点15分 
        DateTimeFormats.LLLL -> s"${YEAR}年${MONTH}月${DAY}日$WEEKDAY_LONG$AM_PM{{$IN|date:h}}点{{$IN|date:mm}}分",
        
        // YYYY年M月D日dddd HH:mm, e.g. 2021年1月3日星期日 14:15 
        DateTimeFormats.llll -> s"${YEAR}年${MONTH}月${DAY}日$WEEKDAY_LONG $HH24_MM"
  )
}

