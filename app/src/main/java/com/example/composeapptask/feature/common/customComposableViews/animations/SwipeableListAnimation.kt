package com.example.composeapptask.feature.common.customComposableViews.animations

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.composeapptask.ui.theme.AppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> DragDropSwipeColumn(
    items: MutableList<T>,
    onSwap: (Int, Int) -> Unit,
    onDelete: (T) -> Unit,
    onComplete: (T) -> Unit,
    itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) {
    var overscrollJob by remember { mutableStateOf<Job?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val dragDropState = rememberDragDropState(listState) { fromIndex, toIndex ->
        onSwap(fromIndex, toIndex)
    }

    LazyColumn(
        modifier = Modifier
            .pointerInput(dragDropState) {
                detectDragGesturesAfterLongPress(
                    onDrag = { change, offset ->
                        change.consume()
                        dragDropState.onDrag(offset = offset)

                        if (overscrollJob?.isActive == true)
                            return@detectDragGesturesAfterLongPress

                        dragDropState
                            .checkForOverScroll()
                            .takeIf { it != 0f }
                            ?.let {
                                overscrollJob =
                                    scope.launch {
                                        dragDropState.state.animateScrollBy(
                                            it * 1.3f, tween(easing = LinearEasing)
                                        )
                                    }
                            }
                            ?: run { overscrollJob?.cancel() }
                    },
                    onDragStart = { offset -> dragDropState.onDragStart(offset) },
                    onDragEnd = {
                        dragDropState.onDragInterrupted()
                        overscrollJob?.cancel()
                    },
                    onDragCancel = {
                        dragDropState.onDragInterrupted()
                        overscrollJob?.cancel()
                    }
                )
            },
        state = listState,
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        itemsIndexed(
            items = items,
            key = { index, _ -> index }
        ) { index, item ->
            val dismissState = rememberSwipeToDismissBoxState(
                confirmValueChange = { newValue ->
                    if (newValue == SwipeToDismissBoxValue.EndToStart ||
                        newValue == SwipeToDismissBoxValue.StartToEnd
                    ) {
                        scope.launch {
                            when (newValue) {
                                SwipeToDismissBoxValue.EndToStart -> {
                                    val removedItem = items[index]
                                    items.removeAt(index)
                                    onDelete(removedItem)
                                }
                                SwipeToDismissBoxValue.StartToEnd -> {
                                    onComplete(items[index])
                                }
                                else -> {}
                            }
                        }
                        true
                    } else {
                        false
                    }
                }
            )

            LaunchedEffect(dismissState.currentValue) {
                if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd ||
                    dismissState.currentValue == SwipeToDismissBoxValue.EndToStart
                ) {
                    dismissState.reset()
                }
            }

            SwipeToDismiss(
                state = dismissState,
                directions = setOf(
                    SwipeToDismissBoxValue.StartToEnd,
                    SwipeToDismissBoxValue.EndToStart
                ),
                background = {
                    val targetValue = dismissState.targetValue
                    val backgroundColor = when (targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> AppTheme.colors.baseColorScheme.error
                        SwipeToDismissBoxValue.StartToEnd -> AppTheme.colors.baseColorScheme.secondary
                        else -> Color.Transparent
                    }
                    val icon = when (targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Icons.Filled.Delete
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Filled.Check
                        else -> null
                    }
                    val iconAlignment = when (targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                        SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                        else -> Alignment.Center
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(backgroundColor)
                            .padding(horizontal = 20.dp),
                        contentAlignment = iconAlignment
                    ) {
                        icon?.let {
                            Icon(
                                imageVector = it,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                },
                dismissContent = {
                    DraggableItem(
                        dragDropState = dragDropState,
                        index = index
                    ) {
                        itemContent(item)
                    }
                }
            )
        }
    }
}