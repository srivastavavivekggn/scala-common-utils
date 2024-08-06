package com.srivastavavivekggn.scala.util.lang.i18n

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.{INPUT => IN, DateTimeFormats}
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.CommonFormats._
    
/**
  * pt-BR locale detail
  */
class PortugueseBrLocaleDetail extends AbstractLocaleDetail(LocaleUtils.getValidLocale("pt-BR")) {

  override val numbers: List[String] = List(
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
  )

  override val months: List[String] = List(
      "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
  )

  override val monthsShort: List[String] = List(
      "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"
  )

  override val weekdays: List[String] = List(
      "Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"
  )

  override val weekdaysShort: List[String] = List(
      "Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sáb"
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
        
        // D [de] MMMM [de] YYYY, e.g. 3 de Janeiro de 2021 
        DateTimeFormats.LL -> s"$DAY de $MONTH_LONG de $YEAR",
        
        // D [de] MMM [de] YYYY, e.g. 3 de Jan de 2021 
        DateTimeFormats.ll -> s"$DAY de $MONTH_SHORT de $YEAR",
        
        // D [de] MMMM [de] YYYY [às] HH:mm, e.g. 3 de Janeiro de 2021 às 14:15 
        DateTimeFormats.LLL -> s"$DAY de $MONTH_LONG de $YEAR às $HH24_MM",
        
        // D [de] MMM [de] YYYY [às] HH:mm, e.g. 3 de Jan de 2021 às 14:15 
        DateTimeFormats.lll -> s"$DAY de $MONTH_SHORT de $YEAR às $HH24_MM",
        
        // dddd, D [de] MMMM [de] YYYY [às] HH:mm, e.g. Domingo, 3 de Janeiro de 2021 às 14:15 
        DateTimeFormats.LLLL -> s"$WEEKDAY_LONG, $DAY de $MONTH_LONG de $YEAR às $HH24_MM",
        
        // ddd, D [de] MMM [de] YYYY [às] HH:mm, e.g. Dom, 3 de Jan de 2021 às 14:15 
        DateTimeFormats.llll -> s"$WEEKDAY_SHORT, $DAY de $MONTH_SHORT de $YEAR às $HH24_MM"
  )
}

