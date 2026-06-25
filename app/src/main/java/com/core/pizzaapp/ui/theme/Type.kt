package com.core.pizzaapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.core.pizzaapp.R

val FigtreeFamily = FontFamily(
    Font(R.font.figtree_light, FontWeight.Light),
    Font(R.font.figtree_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.figtree_regular, FontWeight.Normal),
    Font(R.font.figtree_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.figtree_medium, FontWeight.Medium),
    Font(R.font.figtree_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.figtree_semi_bold, FontWeight.SemiBold),
    Font(R.font.figtree_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.figtree_bold, FontWeight.Bold),
    Font(R.font.figtree_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.figtree_extra_bold, FontWeight.ExtraBold),
    Font(R.font.figtree_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.figtree_black, FontWeight.Black),
    Font(R.font.figtree_black_italic, FontWeight.Black, FontStyle.Italic),
)

val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.64).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FigtreeFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
    ),
)
