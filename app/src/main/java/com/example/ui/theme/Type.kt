package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// Custom Urdu Nastaleeq Font Family from resources
var JameelNooriNastaleeq: FontFamily = FontFamily.SansSerif

// Set of Material typography styles mapped to Nastaleeq
val Typography =
  Typography(
    bodyLarge =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
      ),
    titleMedium =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
      ),
    labelSmall =
      TextStyle(
        fontFamily = JameelNooriNastaleeq,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
      )
  )

