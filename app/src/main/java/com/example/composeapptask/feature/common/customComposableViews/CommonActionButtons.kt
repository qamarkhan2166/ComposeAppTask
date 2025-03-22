package com.example.composeapptask.feature.common.customComposableViews

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composeapptask.ui.theme.AppTheme

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
internal fun BouncingFAB(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.8f else 1f,
        animationSpec = tween(durationMillis = 100),
        finishedListener = {
            isPressed = false
        }, label = ""
    )

    FloatingActionButton(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .scale(scale)
            .padding(16.dp),
        containerColor = AppTheme.colors.customPrimary,
        shape = CircleShape
    ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
    }
}
