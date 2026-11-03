package br.edu.estudamais.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AzulProfundo = Color(0xFF17372F)
val VerdeSuave = Color(0xFFB5EDDA)
private val Claro = lightColorScheme(
    primary = Color(0xFF006B57), onPrimary = Color.White,
    primaryContainer = Color(0xFFD1F2E6), onPrimaryContainer = AzulProfundo,
    secondary = Color(0xFF52655D), background = Color(0xFFF6F8F5),
    surface = Color(0xFFF6F8F5), surfaceContainerLow = Color.White,
    surfaceContainer = Color(0xFFEBF1EC), onSurface = Color(0xFF17261F),
    outlineVariant = Color(0xFFD5DFD7)
)
private val Escuro = darkColorScheme(
    primary = VerdeSuave, onPrimary = AzulProfundo,
    primaryContainer = Color(0xFF205445), onPrimaryContainer = Color(0xFFCBF4E5),
    background = Color(0xFF101A16), surface = Color(0xFF101A16),
    surfaceContainerLow = Color(0xFF18241E), surfaceContainer = Color(0xFF233128)
)
private val Tipografia = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 28.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
)
@Composable
fun EstudaMaisTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Escuro else Claro, typography = Tipografia, content = content)
}
