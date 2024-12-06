package com.example.composeapptask.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composeapptask.R
import com.example.composeapptask.ui.theme.AppTheme
import com.example.composeapptask.feature.common.customComposableViews.MediumTitleText
import com.example.composeapptask.feature.common.customComposableViews.TitleText

@Composable
fun LoginScreen(onNavigateForward: () -> Unit) {

    val viewModel: LoginScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color.White,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            LoginScreenContent(
                onClick = { viewModel.onClickLogin(onProceedNext = onNavigateForward) },
                onPasswordChange = viewModel::onPasswordChange,
                onEmailChange = viewModel::onEmailChange,
                uiState = uiState
            )
        }
    }
}

@Composable
private fun LoginScreenContent(
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onClick: () -> Unit,
    uiState: LoginUiState
) {

    Column(
        modifier = Modifier
            .padding(horizontal = AppTheme.dimens.paddingLarge)
            .padding(bottom = AppTheme.dimens.paddingExtraLarge)
    ) {

        // Heading Jetpack Compose
        MediumTitleText(
            modifier = Modifier
                .padding(top = AppTheme.dimens.paddingLarge)
                .fillMaxWidth(),
            text = stringResource(id = R.string.jetpack_compose),
            textAlign = TextAlign.Center
        )

        // Login Logo
        Image(
            painter = painterResource(id = R.drawable.jetpack_compose_logo),
            contentDescription = stringResource(id = R.string.login_heading_text),
            modifier = Modifier
                .fillMaxWidth()
                .height(128.dp)
                .padding(top = AppTheme.dimens.paddingSmall),
        )

        // Heading Login
        TitleText(
            modifier = Modifier.padding(top = AppTheme.dimens.paddingLarge),
            text = stringResource(id = R.string.login_heading_text)
        )

        // Login Inputs Composable
        LoginInputs(
            loginState = uiState,
            onEmailOrMobileChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onSubmit = onClick,
        )

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreenContent(
        onPasswordChange = {},
        onEmailChange = {},
        onClick = {},
        uiState = LoginUiState()
    )
}