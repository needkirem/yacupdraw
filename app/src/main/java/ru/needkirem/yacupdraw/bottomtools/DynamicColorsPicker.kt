package ru.needkirem.yacupdraw.bottomtools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

@Composable
fun DynamicColorsPicker(
    viewModel: BottomToolsViewModel,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color.Red,
        Color.White,
        Color.Yellow,
        Color.Green,
        Color.Cyan,
        Color.Blue,
        Color.Magenta,
        Color.Red,
        Color.Black
    )

    var sliderPosition by remember {
        mutableFloatStateOf(getSliderPositionForColor(viewModel.currentColor.value, colors))
    }

    val adjustedPosition = sliderPosition.coerceIn(0f, 1f)
    val colorIndex = ((adjustedPosition * (colors.size - 1)).toInt()).coerceIn(0, colors.size - 2)
    val colorFraction = adjustedPosition * (colors.size - 1) - colorIndex
    val displayedColor = lerp(colors[colorIndex], colors[colorIndex + 1], colorFraction)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .clip(RoundedCornerShape(100))
                .background(
                    brush = Brush.horizontalGradient(colors)
                )
        )
        Slider(
            value = sliderPosition,
            onValueChange = {
                sliderPosition = it
                viewModel.selectColor(displayedColor)
            },
            colors = SliderDefaults.colors(
                thumbColor = displayedColor,
                activeTrackColor = Color.Transparent,
                inactiveTrackColor = Color.Transparent,
                disabledThumbColor = Color.Black
            )
        )

    }

}


private fun getSliderPositionForColor(inputColor: Color, colors: List<Color>): Float {
    var closestIndex = 0F
    var closestDistance = Float.MAX_VALUE

    for (i in 0 until colors.size - 1) {
        val startColor = colors[i]
        val endColor = colors[i + 1]

        // Check multiple points between startColor and endColor
        for (weight in 0..100) {
            val fraction = weight / 100f
            val candidateColor = lerp(startColor, endColor, fraction)
            val distance = colorDistance(inputColor, candidateColor)

            if (distance < closestDistance) {
                closestDistance = distance
                closestIndex = i + fraction
            }
        }
    }
    return closestIndex / (colors.size - 1)
}


private fun colorDistance(c1: Color, c2: Color): Float {
    // Calculate the Euclidean distance between two colors
    return sqrt(
        (c1.red - c2.red).pow(2) +
                (c1.green - c2.green).pow(2) +
                (c1.blue - c2.blue).pow(2)
    )
}
