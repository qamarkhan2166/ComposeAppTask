package com.example.composeapptask.feature.common.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember

object ListUtils {

    /**
     * Example usage of movable content in a list:
     *
     * // 1. Create movable content from the list
     * val listComposable = actualList.movable { item ->
     *     Counter(text = item) // Your composable content
     * }
     *
     * // 2. Use the movable content in your layout
     * Column(modifier = Modifier.weight(1f)) {
     *     list.forEach { item ->
     *         listComposable(item) // Reuse the movable content
     *     }
     * }
     *
     * Note: This pattern helps improve recomposition performance by making
     * the content movable rather than recreating it each time.
     */

    @Composable
    fun <T> List<T>.movable(
        transform: @Composable (item: T) -> Unit
    ): @Composable (item: T) -> Unit {
        val composedItems = remember(this) { mutableMapOf<T, @Composable () -> Unit>() }
        return { item: T ->
            composedItems.getOrPut(item) {
                movableContentOf { transform(item) }
            }.invoke()
        }
    }

    /**
     * Creates movable content for any composable, with or without parameters.
     *
     * @param content The composable block to make movable.
     * @return A reusable composable function.
     *
     * val movableComponents = rememberMovableContent {
     *         Component()
     *         Divider()
     *     }
     *
     * Row(Modifier.weight(1f)) { movableLetterBoxes() }
     */
    @Composable
    fun rememberMovableContent(
        content: @Composable () -> Unit
    ): @Composable () -> Unit {
        return remember { movableContentOf(content) }
    }

}