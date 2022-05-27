package com.naky.circletimer.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CircleTimer(
    radius: Dp = 80.dp,
    numberOfMarker: Int = 40,
    stroke: Float = 2f,
    lineStart: Float = 1f,
    lineEnd: Float = 10f,
    colors: Color = MaterialTheme.colors.onBackground
) {
    val arcDegrees = 360
    val startStepAngle = 90
    val degreesMarkerStep = arcDegrees / numberOfMarker

    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutLinearInEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(
        modifier = Modifier
            .size(radius * 2)
            .aspectRatio(1f)
            .alpha(alpha),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = (drawContext.size.width)
                val h = (drawContext.size.height)

                val paint = Paint().apply {
                    color = colors
                }

                for ((_, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = lineEnd
                    paint.color = colors
                    val lineStartX = lineStart
                    paint.strokeWidth = stroke
                    canvas.save()
                    canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
                    canvas.drawLine(
                        Offset(lineStartX, h / 2f),
                        Offset(lineEndX, h / 2f),
                        paint
                    )
                    canvas.restore()
                }
            }
        }
    )
}