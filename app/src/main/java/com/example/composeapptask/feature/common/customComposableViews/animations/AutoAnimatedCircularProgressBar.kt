package com.example.composeapptask.feature.common.customComposableViews.animations

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
fun AutoAnimatedCircularProgressBar(
    modifier: Modifier = Modifier,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 2000, // 2 seconds
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    )
) {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    val textMeasurer = remember(density, fontFamilyResolver, layoutDirection) {
        TextMeasurer(
            defaultDensity = density,
            defaultFontFamilyResolver = fontFamilyResolver,
            defaultLayoutDirection = layoutDirection
        )
    }

    val animatedProgress = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = animationDuration))
        }
    }

    Canvas(modifier = modifier.size(120.dp)) {
        drawCircle(
            color = backgroundColor,
            radius = size.minDimension / 2,
            style = Stroke(width = strokeWidth.toPx())
        )

        drawCircularProgressIndicator(
            progress = animatedProgress.value,
            color = progressColor,
            strokeWidth = strokeWidth.toPx()
        )

        val text = "${(animatedProgress.value * 100).toInt()}%"
        drawTextInCenter(text, textColor, textStyle, textMeasurer)
    }
}

private fun DrawScope.drawTextInCenter(
    text: String,
    color: Color,
    textStyle: TextStyle,
    textMeasurer: TextMeasurer
) {
    val textLayoutResult = textMeasurer.measure(
        text = androidx.compose.ui.text.AnnotatedString(text),
        style = textStyle
    )
    val textOffset = Offset(
        x = size.width / 2 - textLayoutResult.size.width / 2,
        y = size.height / 2 - textLayoutResult.size.height / 2
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = textOffset,
        color = color
    )
}

private fun DrawScope.drawCircularProgressIndicator(
    progress: Float,
    color: Color,
    strokeWidth: Float
) {
    val sweepAngle = progress * 360
    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle = sweepAngle,
        useCenter = false,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
    )
}

@Preview(showBackground = true)
@Composable
private fun AutoAnimatedProgressScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AutoAnimatedCircularProgressBar(
            progressColor = Color.Green,
            backgroundColor = Color.LightGray,
            strokeWidth = 12.dp,
            animationDuration = 2000
        )
    }
}