package com.example.composeapptask.feature.common.customComposableViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapptask.R
import com.example.composeapptask.ui.theme.AppTheme

//################################################
//##### Interactive Task Manager Component #######
//################################################

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithLeftAndRightIcon(
    height: Dp = 52.dp,
    title: String = "",
    backgroundColor: Color = Color.White,
    leftIcon: Int = R.drawable.ic_back,
    onNavigationUp: (() -> Unit)? = { },
    rightIcon: Int? = R.drawable.baseline_info_outline_24,
    rightText: String? = null,
    rightAction: (() -> Unit) = { },
    subtitle: AnnotatedString? = null,
    rightLayout: (@Composable RowScope.() -> Unit)? = null,
) {
    TopAppBar(
        modifier = Modifier.height(if (subtitle == null) height else 95.dp),
        windowInsets = WindowInsets(
            top = 0.dp,
            bottom = 0.dp
        ),
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .padding(end = 10.dp)
                    .height(height)
                    .background(backgroundColor),
                verticalAlignment = Alignment.CenterVertically

            ) {
                /** navigation icon **/
                if (onNavigationUp != null) {
                    val vector = ImageVector.vectorResource(id = leftIcon)
                    Icon(
                        rememberVectorPainter(image = vector),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier
                            .width(12.dp)
                            .height(13.dp)
                            .clickable { onNavigationUp.invoke() }
                    )
                }
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(start = 7.dp, top = 3.dp)
                        .weight(1f),
                    color = AppTheme.colors.black,
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 16.sp)
                )

                rightLayout?.invoke(this) ?: run {
                    rightIcon?.let {
                        Image(
                            painter = painterResource(rightIcon),
                            contentDescription = null,
                            modifier = Modifier
                                .width(37.dp)
                                .height(37.dp)
                                .padding(end = 8.dp)
                                .clickable { rightAction.invoke() }
                        )
                    }

                    rightText?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = rightText,
                            style = AppTheme.typography.labelMedium,
                            modifier = Modifier
                                .clickable { rightAction.invoke() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun TopBarWithLeftAndRightIcon_Preview() {
    TopBarWithLeftAndRightIcon(
        title = "Filter Screen",
        rightIcon = null,
        rightText = stringResource(R.string.app_name)
    )
}
