package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

private val DarkColorScheme =
  darkColorScheme(
    primary = IslamicGold,
    secondary = Emerald80,
    tertiary = Gold80,
    background = IslamicDarkGreen,
    surface = IslamicDarkGreen,
    onPrimary = IslamicDarkGreen,
    onSecondary = IslamicDarkGreen,
    onBackground = IslamicCream,
    onSurface = IslamicCream
  )

private val LightColorScheme =
  lightColorScheme(
    primary = IslamicGreen,
    secondary = IslamicGold,
    tertiary = Gold40,
    background = IslamicCream,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = IslamicDarkGreen,
    onSurface = IslamicDarkGreen
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  // Safely load typeface on the native platform side to avoid Compose lazy-loading crashes
  val customFontFamily = remember(context) {
    try {
      val typeface = androidx.core.content.res.ResourcesCompat.getFont(context, R.font.jameel_noori_nastaleeq_real)
      if (typeface != null) {
        FontFamily(typeface)
      } else {
        FontFamily.SansSerif
      }
    } catch (e: Exception) {
      e.printStackTrace()
      FontFamily.SansSerif
    }
  }

  // Create typography with the custom loaded font family
  val dynamicTypography = remember(customFontFamily) {
    androidx.compose.material3.Typography(
      bodyLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
      ),
      bodyMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
      ),
      titleLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
      ),
      titleMedium = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
      ),
      labelLarge = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
      ),
      labelSmall = TextStyle(
        fontFamily = customFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
      )
    )
  }

  MaterialTheme(colorScheme = colorScheme, typography = dynamicTypography, content = content)
}
