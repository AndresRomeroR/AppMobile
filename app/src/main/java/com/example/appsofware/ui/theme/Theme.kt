// app/src/main/java/com/example/appsofware/ui/theme/Theme.kt
package com.example.appsofware.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightPinkScheme = lightColorScheme(
    primary            = Pink40,
    onPrimary          = Color.White,
    primaryContainer   = Pink80,
    onPrimaryContainer = Pink10,
    secondary          = Pink30,
    onSecondary        = Color.White,
    secondaryContainer = Pink80,
    onSecondaryContainer = Pink10,
    background         = PinkBackground,
    onBackground       = Color.Black,
    surface            = Color.White,
    onSurface          = Color.Black,
    outline            = Pink40
)

private val DarkPinkScheme = darkColorScheme(
    primary            = Pink40Dark,
    onPrimary          = Color.White,
    primaryContainer   = Pink30Dark,
    onPrimaryContainer = Color.White,
    secondary          = Pink30Dark,
    onSecondary        = Color.White,
    secondaryContainer = Pink10Dark,
    onSecondaryContainer = Color.White,
    background         = PinkBackgroundDark,
    onBackground       = Color.White,
    surface            = Pink10Dark,
    onSurface          = Color.White,
    outline            = Pink40Dark
)

@Composable
fun AppSofwareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkPinkScheme else LightPinkScheme,
        typography  = Typography(),
        shapes      = Shapes(),
        content     = content
    )
}
