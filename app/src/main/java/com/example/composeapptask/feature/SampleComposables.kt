package com.example.composeapptask.feature

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.composeapptask.ui.theme.AppTheme

@Composable
fun AllChatScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "AllChatScreen",
            color = AppTheme.colors.black,
            style = AppTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ProfileScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "ProfileScreen",
            color = AppTheme.colors.black,
            style = AppTheme.typography.bodyLarge
        )
    }
}