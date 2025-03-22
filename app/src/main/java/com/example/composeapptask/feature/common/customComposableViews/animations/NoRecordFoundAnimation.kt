package com.example.composeapptask.feature.common.customComposableViews.animations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.composeapptask.R
import com.example.composeapptask.ui.theme.AppTheme

//################################################
//##### Interactive Task Manager Component #######
//################################################

@Composable
internal fun NoContentFound(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            NoRecordFoundAnimation(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )

            Text(
                text = text,
                style = AppTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = AppTheme.colors.black,
                modifier = Modifier.padding(
                    top = 14.dp,
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 12.dp,
                ),
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
            )
        }
    }

}

@Composable
private fun NoRecordFoundAnimation(modifier: Modifier = Modifier.fillMaxSize()) {

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.no_records_found_animation))

    LottieAnimation(
        modifier = modifier,
        composition = composition,
        iterations = LottieConstants.IterateForever,
    )
}


@Preview(showBackground = true)
@Composable
private fun NoContentFoundPreview()
{
    NoContentFound(text = "No Data Found")
}