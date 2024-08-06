package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * ru-RU locale detail
  */
class RussianRuLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("ru-RU")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"
  )

  override val monthsShort: List[String] = List(
      "янв.", "февр.", "мар.", "апр.", "мая", "июня", "июля", "авг.", "сент.", "окт.", "нояб.", "дек."
  )

  override val weekdays: List[String] = List(
      "воскресенье", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота"
  )

  override val weekdaysShort: List[String] = List(
      "вс", "пн", "вт", "ср", "чт", "пт", "сб"
  )

  override val amPm: List[String] = List(
      "ночи", "дня"
  )
  
  override val standardFormats = super.standardFormats ++ Map(
    
        // H:mm, e.g. 14:15 
        DateTimeFormats.LT -> s"$H24_MM",
        
        // H:mm:ss, e.g. 14:15:44 
        DateTimeFormats.LTS -> s"$H24_MM_SS",
        
        // DD.MM.YYYY, e.g. 03.01.2021 
        DateTimeFormats.L -> s"$DAY2.$MONTH2.$YEAR",
        
        // DD.MM.YYYY, e.g. 3.1.2021 
        DateTimeFormats.l -> s"$DAY.$MONTH.$YEAR",
        
        // D MMMM YYYY г., e.g. 3 января 2021 г. 
        DateTimeFormats.LL -> s"$DAY $MONTH_LONG $YEAR г.",
        
        // D MMM YYYY г., e.g. 3 янв. 2021 г. 
        DateTimeFormats.ll -> s"$DAY $MONTH_SHORT $YEAR г.",
        
        // D MMMM YYYY г., H:mm, e.g. 3 января 2021 г., 14:15 
        DateTimeFormats.LLL -> s"$DAY $MONTH_LONG $YEAR г., $H24_MM",
        
        // D MMM YYYY г., H:mm, e.g. 3 янв. 2021 г., 14:15 
        DateTimeFormats.lll -> s"$DAY $MONTH_SHORT $YEAR г., $H24_MM",
        
        // dddd, D MMMM YYYY г., H:mm, e.g. воскресенье, 3 января 2021 г., 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY $MONTH_LONG $YEAR г., $H24_MM",
        
        // ddd, D MMM YYYY г., H:mm, e.g. вс, 3 янв. 2021 г., 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY $MONTH_SHORT $YEAR г., $H24_MM"
  )
}

