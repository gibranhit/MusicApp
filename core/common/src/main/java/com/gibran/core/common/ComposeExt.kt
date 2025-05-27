package com.gibran.core.common

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import com.gibran.core.common.ComposeConstants.PrimaryShimmer
import com.gibran.core.common.ComposeConstants.SecondaryShimmer
import com.gibran.core.common.ComposeConstants.animationTime
import com.gibran.core.common.ComposeConstants.initialValueTransaction
import com.gibran.core.common.ComposeConstants.targetValueTransaction

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember {
        mutableStateOf(IntSize.Zero)
    }
    val transition = rememberInfiniteTransition(label = "")
    val startOffsetX by transition.animateFloat(
        initialValue = initialValueTransaction * size.width.toFloat(),
        targetValue = targetValueTransaction * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(animationTime)
        ),
        label = ""
    )

    background(
        brush = Brush.linearGradient(
            colors = listOf(
                PrimaryShimmer,
                SecondaryShimmer,
                PrimaryShimmer
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
        )
    )
        .onGloballyPositioned {
            size = it.size
        }
}

object ComposeConstants {
    const val animationTime = 1000
    const val initialValueTransaction = -2
    const val targetValueTransaction = 2
    val PrimaryShimmer = Color(0xFFB8B5B5)
    val SecondaryShimmer = Color(0xFF8F8B8B)
}
