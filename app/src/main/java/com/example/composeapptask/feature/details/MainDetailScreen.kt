package com.example.composeapptask.feature.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.customComposableViews.AppLoaderLayout
import com.example.composeapptask.feature.dao.UserMedicationResponse
import com.example.composeapptask.feature.dao.getAllAssociatedDrugs
import com.example.composeapptask.ui.theme.AppTheme

@Composable
fun MainDetailScreen(onNavigateBack: () -> Unit) {
    val viewModel: MainDetailViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        containerColor = Color.Red,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            MainDetailScreenContent(
                uiState = uiState,
                onClick = onNavigateBack
            )
        }
        AppLoaderLayout(showLoader = uiState.isLoading, isSemiTransparent = true)
    }
}

@Composable
private fun MainDetailScreenContent(
    onClick: () -> Unit,
    uiState: MainDetailUiState
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Text(
            modifier = Modifier.fillMaxWidth()
                .padding(top = AppTheme.dimens.paddingSmall)
                .align(alignment = Alignment.Start),
            text = "${stringResource(id = R.string.login_email_id_or_phone_label)}: ${uiState.emailOrMobile}",
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.bodyMedium
        )

        MedicineSection(userMedicationResponse = uiState.userMedicationResponse)
        Spacer(Modifier.weight(1f))
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Text(stringResource(R.string.done))
        }
    }
}

@Composable
private fun ColumnScope.MedicineSection(
    userMedicationResponse: UserMedicationResponse?
) {
    val nonNullUserMedicationResponse = userMedicationResponse ?: return
    Text(
        modifier = Modifier.fillMaxWidth()
            .padding(top = AppTheme.dimens.paddingSmall)
            .align(alignment = Alignment.Start),
        text = stringResource(id = R.string.medication),
        color = MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        ),
    )
    LazyColumn(
        Modifier
            .fillMaxWidth().wrapContentHeight().padding(top = 30.dp)
    ) {
        items(
            items = nonNullUserMedicationResponse.getAllAssociatedDrugs(),
            itemContent = { item ->
                RowText(
                    title = item.name.orEmpty(),
                    value = item.strength.orEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        )
    }
}

@Composable
private fun RowText(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    valueStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    title: String,
    titleModifier: Modifier = Modifier,
    valueModifier: Modifier = Modifier,
    titleStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    rowArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically
) {
    Row(
        horizontalArrangement = rowArrangement,
        modifier = modifier,
        verticalAlignment = verticalAlignment
    ) {
        Text(
            title,
            style = titleStyle,
            textAlign = TextAlign.Start,
            modifier = titleModifier,
            color = Color.Black,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        )
        Text(
            value,
            modifier = valueModifier
                .padding(
                    start = 5.dp
                ),
            style = valueStyle,
            textAlign = TextAlign.End,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainDetailScreenContent() {
    MainDetailScreenContent(
        onClick = {},
        uiState = MainDetailUiState(
            emailOrMobile = "abc@gmail.com"
        )
    )
}