package com.example.composeapptask.feature.common.customComposableViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.composeapptask.ui.theme.AppTheme

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
fun KeyValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.black.copy(alpha = 0.7f)
        )
    }
}
