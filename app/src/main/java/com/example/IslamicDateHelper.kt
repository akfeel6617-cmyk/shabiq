package com.example

import android.content.Context
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.Locale

object IslamicDateHelper {

    data class DatePanelData(
        val dayName: String,
        val dayNameUrdu: String,
        val dayNameEnglish: String,
        val gregDay: String,
        val gregMonth: String,
        val gregYear: String,
        val hijriDay: String,
        val hijriMonth: String,
        val hijriYear: String,
        val hijriSuffix: String
    )

    fun calculateDatePanelData(context: Context, hijriOffset: Int, lang: String): DatePanelData {
        val localDate = LocalDate.now()
        val dayOfWeek = localDate.dayOfWeek.value // 1 = Monday, 7 = Sunday
        val dayStr = getWeekdayName(dayOfWeek, lang)
        val dayUrdu = getWeekdayName(dayOfWeek, "ur")
        val dayEnglish = getWeekdayName(dayOfWeek, "en")

        val gregDayNum = localDate.dayOfMonth.toString()
        val gregMonthStr = getGregMonthName(localDate.monthValue, "en").uppercase(Locale.US)
        val gregYearRaw = localDate.year.toString()

        var hijriDayNum = "14"
        var hijriMonthValue = 1
        var hijriYearRaw = "1448"
        try {
            val hijriDate = HijrahDate.now().plus(hijriOffset.toLong(), ChronoUnit.DAYS)
            hijriDayNum = hijriDate.get(ChronoField.DAY_OF_MONTH).toString()
            hijriMonthValue = hijriDate.get(ChronoField.MONTH_OF_YEAR)
            hijriYearRaw = hijriDate.get(ChronoField.YEAR_OF_ERA).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val hijriMonthStr = getHijriMonthName(hijriMonthValue, "ar")

        // Utility helper to convert digits for Urdu/Arabic display
        fun convertDigits(input: String, targetLang: String): String {
            if (targetLang != "ar" && targetLang != "ur") return input
            val urduDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
            val targetDigits = if (targetLang == "ar") arabicDigits else urduDigits
            return input.map { ch ->
                if (ch in '0'..'9') targetDigits[ch - '0'] else ch
            }.joinToString("")
        }

        // Islamic date always in Arabic script & digits
        val formattedHijriDay = convertDigits(hijriDayNum, "ar")
        val formattedHijriYear = convertDigits(hijriYearRaw, "ar") + " هـ"

        // Gregorian date always in English text & digits
        val formattedGregDay = gregDayNum
        val formattedGregYear = gregYearRaw

        return DatePanelData(
            dayName = dayUrdu, // Primary display is Urdu for South Asian brand
            dayNameUrdu = dayUrdu,
            dayNameEnglish = dayEnglish,
            gregDay = formattedGregDay,
            gregMonth = gregMonthStr,
            gregYear = formattedGregYear,
            hijriDay = formattedHijriDay,
            hijriMonth = hijriMonthStr,
            hijriYear = formattedHijriYear,
            hijriSuffix = ""
        )
    }

    fun getWeekdayName(dayOfWeek: Int, lang: String): String {
        return when (lang) {
            "ur" -> when (dayOfWeek) {
                1 -> "پیر"
                2 -> "منگل"
                3 -> "بدھ"
                4 -> "جمعرات"
                5 -> "جمعہ"
                6 -> "ہفتہ"
                7 -> "اتوار"
                else -> ""
            }
            "ar" -> when (dayOfWeek) {
                1 -> "الإثنين"
                2 -> "الثلاثاء"
                3 -> "الأربعاء"
                4 -> "الخميس"
                5 -> "الجمعة"
                6 -> "السبت"
                7 -> "الأحد"
                else -> ""
            }
            else -> when (dayOfWeek) {
                1 -> "MONDAY"
                2 -> "TUESDAY"
                3 -> "WEDNESDAY"
                4 -> "THURSDAY"
                5 -> "FRIDAY"
                6 -> "SATURDAY"
                7 -> "SUNDAY"
                else -> ""
            }
        }
    }

    fun getGregMonthName(monthValue: Int, lang: String): String {
        return when (lang) {
            "ur" -> when (monthValue) {
                1 -> "جنوری"
                2 -> "فروری"
                3 -> "مارچ"
                4 -> "اپریل"
                5 -> "مئی"
                6 -> "جون"
                7 -> "جولائی"
                8 -> "اگست"
                9 -> "ستمبر"
                10 -> "اکتوبر"
                11 -> "نومبر"
                12 -> "دسمبر"
                else -> ""
            }
            "ar" -> when (monthValue) {
                1 -> "يناير"
                2 -> "فبراير"
                3 -> "مارس"
                4 -> "أبريل"
                5 -> "مايو"
                6 -> "يونيو"
                7 -> "يوليو"
                8 -> "أغسطس"
                9 -> "سبتمبر"
                10 -> "أكتوبر"
                11 -> "نوفمبر"
                12 -> "ديسمبر"
                else -> ""
            }
            else -> when (monthValue) {
                1 -> "JANUARY"
                2 -> "FEBRUARY"
                3 -> "MARCH"
                4 -> "APRIL"
                5 -> "MAY"
                6 -> "JUNE"
                7 -> "JULY"
                8 -> "AUGUST"
                9 -> "SEPTEMBER"
                10 -> "OCTOBER"
                11 -> "NOVEMBER"
                12 -> "DECEMBER"
                else -> ""
            }
        }
    }

    fun getHijriMonthName(monthValue: Int, lang: String): String {
        return when (lang) {
            "ur" -> when (monthValue) {
                1 -> "محرم"
                2 -> "صفر"
                3 -> "ربیع الاول"
                4 -> "ربیع الثانی"
                5 -> "جمادی الاول"
                6 -> "جمادی الثانی"
                7 -> "رجب"
                8 -> "شعبان"
                9 -> "رمضان"
                10 -> "شوال"
                11 -> "ذی القعدہ"
                12 -> "ذی الحجہ"
                else -> ""
            }
            "ar" -> when (monthValue) {
                1 -> "محرم"
                2 -> "صفر"
                3 -> "ربيع الأول"
                4 -> "ربيع الآخر"
                5 -> "جمادى الأولى"
                6 -> "جمادى الآخرة"
                7 -> "رجب"
                8 -> "شعبان"
                9 -> "رمضان"
                10 -> "شوال"
                11 -> "ذو القعدة"
                12 -> "ذو الحجة"
                else -> ""
            }
            else -> when (monthValue) {
                1 -> "Muharram"
                2 -> "Safar"
                3 -> "Rabi' al-Awwal"
                4 -> "Rabi' ath-Thani"
                5 -> "Jumada al-Awwal"
                6 -> "Jumada ath-Thani"
                7 -> "Rajab"
                8 -> "Sha'ban"
                9 -> "Ramadan"
                10 -> "Shawwal"
                11 -> "Dhu al-Qi'dah"
                12 -> "Dhu al-Hijjah"
                else -> ""
            }
        }
    }
}
