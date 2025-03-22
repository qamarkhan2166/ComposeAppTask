package com.example.composeapptask.feature.common.customComposableViews


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapptask.ui.theme.AppTheme

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
fun <T> HorizontalRadioButtonWithCardSection(
    items: List<T>,
    setSelectedItem: (T) -> Unit,
    getSelectedItem: () -> T?,
    itemLabel: @Composable (T) -> String = { it.toString() },
    enabled: Boolean = true,
    disabledItemsList: List<T> = emptyList()
) {
    if (items.isEmpty()) {
        return
    }

    GenericRadioButtonGroup(
        items = items,
        setSelectedItem = setSelectedItem,
        getSelectedItem = getSelectedItem,
        itemLabel = itemLabel,
        enabled = enabled,
        disabledItemsList = disabledItemsList
    )
}

@Composable
fun <T> GenericRadioButtonGroup(
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.fillMaxWidth(),
    items: List<T>,
    setSelectedItem: (T) -> Unit,
    getSelectedItem: () -> T?,
    itemLabel: @Composable (T) -> String = { it.toString() },
    enabled: Boolean = true,
    disabledItemsList: List<T> = emptyList()
) {
    var selectedValue by remember (getSelectedItem.invoke()) {
        mutableStateOf(getSelectedItem.invoke())
    }

    Row(
        modifier = modifier.selectableGroup()
            .semantics(mergeDescendants = true) {
                contentDescription = "Radio button group with ${items.size} options"
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            HorizontalRadioOption(
                modifier = Modifier.weight(1f),
                item = item,
                selected = selectedValue == item,
                onSelect = {
                    selectedValue = item
                    setSelectedItem.invoke(item)
                },
                itemLabel = itemLabel,
                enabled = if (disabledItemsList.isEmpty()) enabled else !disabledItemsList.contains(item)
            )
        }
    }
}

@Composable
private fun <T> HorizontalRadioOption(
    modifier: Modifier = Modifier,
    item: T,
    selected: Boolean,
    onSelect: () -> Unit,
    itemLabel: @Composable (T) -> String,
    enabled: Boolean,
) {
    val label = itemLabel.invoke(item).trim()
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onSelect,
                enabled = enabled,
                role = Role.RadioButton
            )
            .padding(horizontal = 8.dp)
            .semantics {
                contentDescription =
                    "$label option ${if (selected) "selected" else "unselected"}"
            }
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
            enabled = enabled,
            colors = RadioButtonDefaults.appDefaultColors(),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = label,
            style = AppTheme.typography.bodyMedium.copy(
                fontSize = 14.sp,
                color = if (enabled) AppTheme.colors.black else AppTheme.colors.black.copy(alpha = 0.38f)
            ),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun RadioButtonDefaults.appDefaultColors(): RadioButtonColors {
    return colors(
        selectedColor = AppTheme.colors.greenColor,
        unselectedColor = AppTheme.colors.grayColor,
        disabledSelectedColor = Color.LightGray,
        disabledUnselectedColor = Color.LightGray,
    )
}

@Preview(showBackground = true)
@Composable
private fun HorizontalRadioButtonWithCardSection_Preview() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        HorizontalRadioButtonWithCardSection(
            items = listOf(
                "Front",
                "Back",
                "Both",
            ),
            getSelectedItem = { "" },
            setSelectedItem = {},
            itemLabel = { it },
            disabledItemsList = listOf("Both")
        )
    }
}

