package com.example.composeapptask.feature.taskify.setting

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.composeapptask.R
import com.example.composeapptask.feature.common.customComposableViews.AppLoaderLayout
import com.example.composeapptask.feature.common.customComposableViews.TopBarWithLeftAndRightIcon
import com.example.composeapptask.ui.theme.AppTheme
import com.example.composeapptask.ui.theme.lightColorsList

@Composable
internal fun SettingScreen(
    navController: NavController,
) {
    val viewModel: SettingViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    SettingScreenContent(onBackAction = { navController.navigateUp() },
        isLoading = uiState.isLoading,
        onColorSelected = { color ->
            color?.let {
                viewModel.savePrimaryColor(color, onChanged = {
                    Toast.makeText(context, context.getString(R.string.color_success_message), Toast.LENGTH_LONG).show()
                })
            }
        })
}

@Composable
private fun SettingScreenContent(
    onBackAction: () -> Unit = {}, isLoading: Boolean, onColorSelected: (Color?) -> Unit
) {
    Scaffold(
        containerColor = AppTheme.colors.white,
        topBar = {
            TopBarWithLeftAndRightIcon(
                title = stringResource(R.string.app_name),
                onNavigationUp = onBackAction,
                rightIcon = null
            )
        },
    ) { innerPadding ->
        var pickerColor: Color? by remember { mutableStateOf(null) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.85f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.select_primary_color),
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.black
                )
                Spacer(Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = lightColorsList,
                        key = { index, _ -> index }
                    ) { _, color ->
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable {
                                    pickerColor = color
                                }
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 60.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(pickerColor ?: AppTheme.colors.white, CircleShape))
                }
            }

            BottomAction(
                onClickNextButton = {
                    onColorSelected(pickerColor)
                }, isContinueEnabled = true
            )
        }
        AppLoaderLayout(showLoader = isLoading, isSemiTransparent = false)
    }
}

@Composable
private fun ColumnScope.BottomAction(
    onClickNextButton: () -> Unit = {}, isContinueEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(0.15f),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
        ) {
            Button(
                onClick = onClickNextButton,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.baseColorScheme.secondary,
                    contentColor = if (isContinueEnabled) AppTheme.colors.white else AppTheme.colors.black
                ),
                shape = RoundedCornerShape(6.dp),
                enabled = isContinueEnabled,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.change_color_btn),
                    style = AppTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview
@Composable
private fun TaskDetailsScreenContentPreview() {
    SettingScreenContent(
        isLoading = false,
        onColorSelected = {},
    )
}
