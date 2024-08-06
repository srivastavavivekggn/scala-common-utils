package com.srivastavavivekggn.scala.util.lang

import java.time.LocalDateTime
import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.lang.LocaleUtils.DateTimeFormat
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.DateTimeFormats._

class LocaleUtilsDateTimeSpec extends BaseUtilSpec {

  // 1111, January 04, 2021 02:15 PM
  val dt = LocalDateTime.of(2021, 1, 4, 14, 15, 44, 0)

  behavior of "LocaleUtils.DateTimeFormat"
  
  
      it must "format dates using standard formats for en-US" in {
        val locale = LocaleUtils.getValidLocale("en-US")
                
        assertResult("2:15 PM")(DateTimeFormat.format(dt, LT, locale))
        assertResult("2:15:44 PM")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("01/04/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("1/4/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("January 4, 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("Jan 4, 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("January 4, 2021 2:15 PM")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("Jan 4, 2021 2:15 PM")(DateTimeFormat.format(dt, lll, locale))
        assertResult("Monday, January 4, 2021 2:15 PM")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("Mon, Jan 4, 2021 2:15 PM")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for en-BG" in {
        val locale = LocaleUtils.getValidLocale("en-BG")
                
        assertResult("2:15 PM")(DateTimeFormat.format(dt, LT, locale))
        assertResult("2:15:44 PM")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("01/04/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("1/4/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("January 4, 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("Jan 4, 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("January 4, 2021 2:15 PM")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("Jan 4, 2021 2:15 PM")(DateTimeFormat.format(dt, lll, locale))
        assertResult("Monday, January 4, 2021 2:15 PM")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("Mon, Jan 4, 2021 2:15 PM")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for zh-CN" in {
        val locale = LocaleUtils.getValidLocale("zh-CN")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("2021/01/04")(DateTimeFormat.format(dt, L, locale))
        assertResult("2021/1/4")(DateTimeFormat.format(dt, l, locale))
        assertResult("2021年1月4日")(DateTimeFormat.format(dt, LL, locale))
        assertResult("2021年1月4日")(DateTimeFormat.format(dt, ll, locale))
        assertResult("2021年1月4日下午2点15分")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("2021年1月4日 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("2021年1月4日星期一下午2点15分")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("2021年1月4日星期一 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for fr-FR" in {
        val locale = LocaleUtils.getValidLocale("fr-FR")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04/01/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4/1/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 janvier 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 janv. 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 janvier 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 janv. 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("lundi 4 janvier 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("lun. 4 janv. 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for fr-CA" in {
        val locale = LocaleUtils.getValidLocale("fr-CA")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("2021-01-04")(DateTimeFormat.format(dt, L, locale))
        assertResult("2021-1-4")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 janvier 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 janv. 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 janvier 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 janv. 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("lundi 4 janvier 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("lun. 4 janv. 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for de-DE" in {
        val locale = LocaleUtils.getValidLocale("de-DE")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04.01.2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4.1.2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4. Januar 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4. Jan. 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4. Januar 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4. Jan. 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("Montag, 4. Januar 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("Mo., 4. Jan. 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for hi-IN" in {
        val locale = LocaleUtils.getValidLocale("hi-IN")
                
        assertResult("दोपहर २:१५ बजे")(DateTimeFormat.format(dt, LT, locale))
        assertResult("दोपहर २:१५:४४ बजे")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("०४/०१/२०२१")(DateTimeFormat.format(dt, L, locale))
        assertResult("४/१/२०२१")(DateTimeFormat.format(dt, l, locale))
        assertResult("४ जनवरी २०२१")(DateTimeFormat.format(dt, LL, locale))
        assertResult("४ जन. २०२१")(DateTimeFormat.format(dt, ll, locale))
        assertResult("४ जनवरी २०२१, दोपहर २:१५ बजे")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("४ जन. २०२१, दोपहर २:१५ बजे")(DateTimeFormat.format(dt, lll, locale))
        assertResult("सोमवार, ४ जनवरी २०२१, दोपहर २:१५ बजे")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("सोम, ४ जन. २०२१, दोपहर २:१५ बजे")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for it-IT" in {
        val locale = LocaleUtils.getValidLocale("it-IT")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04/01/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4/1/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 gennaio 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 gen 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 gennaio 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 gen 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("lunedì 4 gennaio 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("lun 4 gen 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for ja-JP" in {
        val locale = LocaleUtils.getValidLocale("ja-JP")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("2021/01/04")(DateTimeFormat.format(dt, L, locale))
        assertResult("2021/01/04")(DateTimeFormat.format(dt, l, locale))
        assertResult("2021年1月4日")(DateTimeFormat.format(dt, LL, locale))
        assertResult("2021年1月4日")(DateTimeFormat.format(dt, ll, locale))
        assertResult("2021年1月4日 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("2021年1月4日 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("2021年1月4日 月曜日 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("2021年1月4日(月) 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for ko-KR" in {
        val locale = LocaleUtils.getValidLocale("ko-KR")
                
        assertResult("오후 2:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("오후 2:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("2021.01.04.")(DateTimeFormat.format(dt, L, locale))
        assertResult("2021.01.04.")(DateTimeFormat.format(dt, l, locale))
        assertResult("2021년 1월 4일")(DateTimeFormat.format(dt, LL, locale))
        assertResult("2021년 1월 4일")(DateTimeFormat.format(dt, ll, locale))
        assertResult("2021년 1월 4일 오후 2:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("2021년 1월 4일 오후 2:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("2021년 1월 4일 월요일 오후 2:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("2021년 1월 4일 월요일 오후 2:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for ms-MY" in {
        val locale = LocaleUtils.getValidLocale("ms-MY")
                
        assertResult("14.15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14.15.44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04/01/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4/1/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 Januari 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 Jan 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 Januari 2021 pukul 14.15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 Jan 2021 pukul 14.15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("Isnin, 4 Januari 2021 pukul 14.15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("Isn, 4 Jan 2021 pukul 14.15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for nl-NL" in {
        val locale = LocaleUtils.getValidLocale("nl-NL")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04-01-2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4-1-2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 januari 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 jan. 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 januari 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 jan. 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("maandag 4 januari 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("ma. 4 jan. 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for pt-BR" in {
        val locale = LocaleUtils.getValidLocale("pt-BR")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04/01/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4/1/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 de Janeiro de 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 de Jan de 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 de Janeiro de 2021 às 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 de Jan de 2021 às 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("Segunda-feira, 4 de Janeiro de 2021 às 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("Seg, 4 de Jan de 2021 às 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for ru-RU" in {
        val locale = LocaleUtils.getValidLocale("ru-RU")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04.01.2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4.1.2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 января 2021 г.")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 янв. 2021 г.")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 января 2021 г., 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 янв. 2021 г., 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("понедельник, 4 января 2021 г., 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("пн, 4 янв. 2021 г., 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for es-ES" in {
        val locale = LocaleUtils.getValidLocale("es-ES")
                
        assertResult("14:15")(DateTimeFormat.format(dt, LT, locale))
        assertResult("14:15:44")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("04/01/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("4/1/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 de enero de 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 de ene. de 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 de enero de 2021 14:15")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 de ene. de 2021 14:15")(DateTimeFormat.format(dt, lll, locale))
        assertResult("lunes, 4 de enero de 2021 14:15")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("lun., 4 de ene. de 2021 14:15")(DateTimeFormat.format(dt, llll, locale))
      }
    

      it must "format dates using standard formats for es-US" in {
        val locale = LocaleUtils.getValidLocale("es-US")
                
        assertResult("2:15 PM")(DateTimeFormat.format(dt, LT, locale))
        assertResult("2:15:44 PM")(DateTimeFormat.format(dt, LTS, locale))
        assertResult("01/04/2021")(DateTimeFormat.format(dt, L, locale))
        assertResult("1/4/2021")(DateTimeFormat.format(dt, l, locale))
        assertResult("4 de enero de 2021")(DateTimeFormat.format(dt, LL, locale))
        assertResult("4 de ene. de 2021")(DateTimeFormat.format(dt, ll, locale))
        assertResult("4 de enero de 2021 2:15 PM")(DateTimeFormat.format(dt, LLL, locale))
        assertResult("4 de ene. de 2021 2:15 PM")(DateTimeFormat.format(dt, lll, locale))
        assertResult("lunes, 4 de enero de 2021 2:15 PM")(DateTimeFormat.format(dt, LLLL, locale))
        assertResult("lun., 4 de ene. de 2021 2:15 PM")(DateTimeFormat.format(dt, llll, locale))
      }
      
}
