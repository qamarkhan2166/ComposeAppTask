package com.example.composeapptask.feature.common.customComposableViews

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.composeapptask.ui.theme.AppTheme

@Composable
fun TitleText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        text = text,
        textAlign = textAlign,
        style = AppTheme.typography.headlineLarge,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun MediumTitleText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        text = text,
        style = AppTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        textAlign = textAlign
    )
}

@Composable
fun ErrorTextInputField(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = AppTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun LabelTextInputField(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colors.black
    )
}

fun Color.toHex(): String {
    return String.format(
        "#%02X%02X%02X",
        (red * 255).toInt(),
        (green * 255).toInt(),
        (blue * 255).toInt()
    )
}
