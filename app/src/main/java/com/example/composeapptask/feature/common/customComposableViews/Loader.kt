package com.example.composeapptask.feature.common.customComposableViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AppLoaderLayout(
    modifier: Modifier = Modifier,
    isSemiTransparent: Boolean = false,
    alignment: Alignment = Alignment.Center,
    showLoader: Boolean,
) {
    AnimatedVisibility(
        visible = showLoader,
        exit = fadeOut(),
        enter = fadeIn(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    // DO Nothing, this to block touches to be passed behind this view
                }
                .then(modifier)
                .then(
                    if (isSemiTransparent) Modifier.background(Color.LightGray) else Modifier
                ),
            contentAlignment = alignment,
        ) {
            CircularProgressIndicator(
                color = Color.Blue,
                modifier = Modifier.align(
                    Alignment.Center,
                ),
            )
        }
    }
}