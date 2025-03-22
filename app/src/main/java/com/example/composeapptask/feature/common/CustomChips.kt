package com.example.composeapptask.feature.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapptask.ui.theme.AppTheme

@Composable
fun CustomChipsGroup(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                CustomChips(
                    text = option,
                    isSelected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
            }
        }
    }
}

@Composable
fun CustomChips(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AppTheme.colors.baseColorScheme.secondary else AppTheme.colors.baseColorScheme.outline.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color.Black,
        )
    }
}

@Preview
@Composable
fun CustomChipsPreview() {
    var selectedFilter by remember { mutableStateOf("") }
    val filters = listOf("All", "Pending", "Completed")

    CustomChipsGroup(
        title = "Filter by status",
        options = filters,
        selectedOption = selectedFilter,
        onOptionSelected = { selectedFilter = it }
    )
}
