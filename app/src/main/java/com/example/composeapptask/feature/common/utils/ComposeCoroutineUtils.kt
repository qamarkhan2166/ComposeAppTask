package com.example.composeapptask.feature.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.CoroutineScope

@Composable
fun LaunchedEffectOneTime(
    key: Any?,
    useNonPersistentRemember: Boolean = false,
    block: suspend CoroutineScope.() -> Unit,
) {
    // Choose either remember or rememberSaveAble based on the useNonPersistentRemember flag
    val hasRun = if (useNonPersistentRemember) {
        remember(key) { mutableStateOf(false) }
    } else {
        rememberSaveable(key) { mutableStateOf(false) }
    }

    LaunchedEffect(key) {
        if (!hasRun.value) {
            hasRun.value = true
            // Your method to be called
            block()
        }
    }
}

@Composable
fun LaunchedEffectOneTime(
    vararg keys: Any?,
    useNonPersistentRemember: Boolean = false,
    block: suspend CoroutineScope.() -> Unit,
) {

    val hasRun = if (useNonPersistentRemember) {
        remember(*keys) { mutableStateOf(false) }
    } else {
        rememberSaveable(*keys) { mutableStateOf(false) }
    }

    LaunchedEffect(*keys) {
        if (!hasRun.value) {
            hasRun.value = true
            // Your method to be called
            block()
        }
    }
}