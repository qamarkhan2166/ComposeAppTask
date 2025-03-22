package com.jodhpurtechies.composelogin.ui.common.customComposableViews

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
//import androidx.compose.material.icons.outlined.Visibility
// import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.customComposableViews.DatePickerUtils
import com.example.composeapptask.feature.common.customComposableViews.ErrorTextInputField
import com.example.composeapptask.feature.common.customComposableViews.LabelTextInputField
import com.example.composeapptask.feature.common.customComposableViews.MediumTitleText
import com.example.composeapptask.ui.theme.AppTheme


//########################
//#####  Component #######
//########################
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Done
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {
                Icons.Filled.Lock
                Icons.Outlined.Lock
                val visibleIconAndText = Pair(
                    first = Icons.Filled.Lock,
                    second = stringResource(id = R.string.icon_password_visible)
                )

                val hiddenIconAndText = Pair(
                    first = Icons.Filled.Lock,
                    second = stringResource(id = R.string.icon_password_hidden)
                )

                val passwordVisibilityIconAndText =
                    if (isPasswordVisible) visibleIconAndText else hiddenIconAndText

                // Render Icon
                Icon(
                    imageVector = passwordVisibilityIconAndText.first,
                    contentDescription = passwordVisibilityIconAndText.second
                )
            }
        },
        singleLine = true,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }),
        isError = isError,
        supportingText = {
            if (isError) {
                ErrorTextInputField(text = errorText)
            }
        }
    )
}


@Composable
fun EmailTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Next
) {

    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(text = label)
        },
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction
        ),
        isError = isError,
        supportingText = {
            if (isError) {
                ErrorTextInputField(text = errorText)
            }
        }
    )

}

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
fun DecoratedTextField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    isError: Boolean = false,
    errorText: String = "",
    imeAction: ImeAction = ImeAction.Next,
    isLargeInputField: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
    isDate: Boolean = false,
    placeholderText: String = "Place holder",
) {
    val context = LocalContext.current
    val rememberErrorColorState =
        if (isError) MaterialTheme.colorScheme.error else AppTheme.colors.baseColorScheme.tertiaryContainer
    Column(modifier = Modifier.wrapContentSize()) {
        label?.let {
            LabelTextInputField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                text = label,
            )
        }

        if (isLargeInputField) {
            LargeTextInputBox(
                value = value,
                onValueChange = onValueChange,
                borderColor = rememberErrorColorState
            )
        } else {
            BasicTextField(
                value = value,
                modifier = modifier,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = imeAction
                ),
                interactionSource = interactionSource ?: rememberTextFieldPressInteractionSource {
                    if (isDate) {
                        DatePickerUtils.showDatePickerDialog(
                            context = context,
                            initialDateValue = value,
                            onDatePicked = { value ->
                                onValueChange.invoke(value)
                            }
                        )
                    }
                },
                decorationBox = { innerTextField ->
                    Row(
                        Modifier
                            .height(48.dp)
                            .border(1.dp, rememberErrorColorState, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholderText,
                                    style = AppTheme.typography.bodyMedium.copy(color = Color.Gray),
                                )
                            }
                            innerTextField()
                        }
                    }
                },
                textStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 16.sp
                )
            )
        }

        if (isError) {
            ErrorTextInputField(text = errorText)
        }
    }


}

@Composable
fun LargeTextInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    borderColor: Color
) {
    TextField(
        value = value,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Text
        ),
        placeholder = {
            Text(
                text = stringResource(R.string.task_description),
                style = AppTheme.typography.bodyMedium.copy(color = Color.Gray),
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .padding(top = 8.dp)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp)
            ),
        onValueChange = onValueChange,
        singleLine = false,
        shape = RoundedCornerShape(10),
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = Color.Gray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        enabled = true
    )
}

@Composable
fun rememberTextFieldPressInteractionSource(
    isEnabled: Boolean = true,
    onClick: () -> Unit,
): MutableInteractionSource {
    val currentOnClick by rememberUpdatedState(onClick)
    return remember { MutableInteractionSource() }.also { interactionSource ->
        if (isEnabled) {
            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect {
                    if (it is PressInteraction.Release) {
                        currentOnClick.invoke()
                    }
                }
            }
        }
    }
}

@Composable
fun CommonDateOfBirthTextInputComponent(
    getSelectedValue: () -> String?,
    setSelectedValue: (String) -> Unit,
    labelText: String = stringResource(id = R.string.due_date),
    placeholderText: String = stringResource(id = R.string.dob_placeholder),
    isDisableFutureDate: Boolean = false
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val dateOfBirthValue = getSelectedValue()
    DateOfBirthTextInputComponent(
        dateOfBirthValue = dateOfBirthValue,
        openDatePickerSheet = {
            focusManager.clearFocus()
            DatePickerUtils.showDatePickerDialog(
                context = context,
                initialDateValue = dateOfBirthValue,
                onDatePicked = { value ->
                    setSelectedValue(value)
                },
                isDisableFutureDate = isDisableFutureDate
            )
        },
        labelText = labelText,
        placeholderText = placeholderText
    )
}


@Composable
private fun DateOfBirthTextInputComponent(
    dateOfBirthValue: String?,
    openDatePickerSheet: () -> Unit,
    labelText: String,
    placeholderText: String,
) {
    val currentOpenDatePickerSheet by rememberUpdatedState(openDatePickerSheet)
    DecoratedTextField(
        label = labelText,
        value = dateOfBirthValue.orEmpty(),
        interactionSource = rememberTextFieldPressInteractionSource {
            currentOpenDatePickerSheet.invoke()
        },
        onValueChange = { },
        placeholderText = placeholderText
    )
}


@Preview(showBackground = true)
@Composable
private fun CommonDateOfBirthTextInputComponent_Preview() {
    var value by remember { mutableStateOf<String?>(null) }
    Box(modifier = Modifier.padding(16.dp)) {
        CommonDateOfBirthTextInputComponent(
            getSelectedValue = { value },
            setSelectedValue = { value = it }
        )
    }
}