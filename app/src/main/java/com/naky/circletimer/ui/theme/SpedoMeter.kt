package com.naky.circletimer.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.naky.circletimer.ui.CircleTimer
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    var targetValue by remember { mutableStateOf(0f) }
    val progress = remember(targetValue) { Animatable(initialValue = 0f) }
    val scope = rememberCoroutineScope()

    val ctx = LocalContext.current
    val currentHeight = ctx.resources.displayMetrics.heightPixels.dp / LocalDensity.current.density
    var circle1 = currentHeight/2.3f

    var stateClick by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
               stateClick =! stateClick
            }
        ) {
            Text(text = "click")
        }

        if(stateClick){
            Slider(
                value = targetValue,
                onValueChange = { targetValue = it }
            )

            val intValue = targetValue * 55
            Text(
                text = "${intValue.toInt()}"
            )

            Button(
                onClick = {
                    scope.launch {
                        progress.animateTo(
                            targetValue = intValue,
                            animationSpec = tween(
                                durationMillis = 1000,
                                easing = FastOutLinearInEasing
                            )
                        )
                    }
                }
            ) {
                Text(text = "Go")
            }
            Spacer(modifier = Modifier.height(20.dp))
                SpeedoMeter(progress = progress.value.toInt())
        }else{
            Column() {
                CircleTimer(radius = circle1/2f, lineStart = 1f, lineEnd = 30f, stroke = 7f)
            }
        }



    }
}

@Composable
fun SpeedoMeter(
    progress: Int
) {
    val arcDegrees = 360
    val startArcAngle = -90f
    val startStepAngle = 90
    val numberOfMarkers = 60
    val degreesMarkerStep = arcDegrees / numberOfMarkers

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        onDraw = {
            drawIntoCanvas { canvas ->
                val w = drawContext.size.width
                val h = drawContext.size.height
                val centerOffset = Offset(w / 2f, h / 2f)
                val quarterOffset = Offset(w / 4f, h / 4f)

                val (mainColor, secondaryColor) = when {
                    progress < 20 -> Color(0xFFD32F2F) to Color(0xFFFFCDD2)
                    progress < 40 -> Color(0xFFF57C00) to Color(0xFFFFE0B2)
                    else -> Color(0xFF388E3C) to Color(0xFFC8E6C9)
                }
                val paint = Paint().apply {
                    color = mainColor
                }
                val centerArcSize = Size(w / 2f, h / 2f)
                val centerArcStroke = Stroke(20f, 0f, StrokeCap.Round)

                //proges
                drawArc(
                    secondaryColor,
                    startArcAngle,
                    arcDegrees.toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )

                drawArc(
                    mainColor,
                    startArcAngle,
                    (degreesMarkerStep * progress).toFloat(),
                    false,
                    topLeft = quarterOffset,
                    size = centerArcSize,
                    style = centerArcStroke
                )
                //center
                drawCircle(mainColor, 50f, centerOffset)
                drawCircle(Color.White, 25f, centerOffset)
                drawCircle(Color.Black, 20f, centerOffset)

                for ((counter, degrees) in (startStepAngle..(startStepAngle + arcDegrees) step degreesMarkerStep).withIndex()) {
                    val lineEndX = 0f
                    paint.color = mainColor
                    val lineStartX = if (counter % 5 == 0) {
                        paint.strokeWidth = 1f
                        0f
                    } else {
                        paint.strokeWidth = 2f
                        lineEndX * .1f
                    }
                    canvas.save()
                    canvas.rotate(degrees.toFloat(), w / 2f, h / 2f)
                    canvas.drawLine(
                        Offset(lineStartX, h / 2f),
                        Offset(lineEndX, h / 2f),
                        paint
                    )

                    if (counter == progress) {
                        paint.color = Color.Black
                        canvas.drawPath(
                            Path().apply {
                                moveTo(w / 2, (h / 2) - 5)
                                lineTo(w / 2, (h / 2) + 5)
                                lineTo(w / 4f, h / 2)
                                lineTo(w / 2, (h / 2) - 5)
                                close()
                            },
                            paint
                        )
                    }
                    canvas.restore()
                }
            }
        }
    )
}
