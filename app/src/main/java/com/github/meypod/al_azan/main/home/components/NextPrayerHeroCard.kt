package com.github.meypod.al_azan.main.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.meypod.al_azan.R
import com.github.meypod.al_azan.core.domain.model.adhan.nextShariaPrayer
import com.github.meypod.al_azan.core.domain.model.settings.SupportedLocales
import com.github.meypod.al_azan.core.presentation.AlAzanTheme
import kotlinx.coroutines.delay
import kotlin.math.min
import kotlin.random.Random
import kotlin.time.Instant

/**
 * M3 Expressive-style hero card: a circular progress arc showing how much of the current interval
 * has elapsed since the last prayer, with a tall countdown to the next prayer in the middle and a
 * "next: <prayer name>" caption below. Lives below the calendar header.
 *
 * [countdownText] is recomputed on each tick by the caller so this composable stays pure; the
 * arc animates from its previous progress to the new one (330ms ease) so when the tick fires the
 * surrounding layout isn't jittery.
 */
@Composable
fun NextPrayerHeroCard(
    countdownText: String,
    nextPrayerLabel: String,
    refreshTick: Instant,
    elapsedFraction: Float,
    modifier: Modifier = Modifier,
) {
    val animatedFraction by animateFloatAsState(
        targetValue = elapsedFraction.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "next-prayer-progress",
    )
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.element_padding), vertical = dimensionResource(R.dimen.element_padding)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.element_padding)),
        ) {
            val (primary, onPrimary) = if (animatedFraction > 0.85f) {
                listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.onTertiary)
            } else {
                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimary)
            }.let { it[0] as Color to it[1] as Color }
            CircularProgressArc(
                progress = animatedFraction,
                tint = primary,
                track = MaterialTheme.colorScheme.surfaceContainerHighest,
                modifier = Modifier.size(96.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        countdownText,
                        style = MaterialTheme.typography.titleLarge,
                        color = onPrimary,
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Next prayer",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    nextPrayerLabel,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun CircularProgressArc(
    progress: Float,
    tint: Color,
    track: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .aspectRatio(1f)
        ) {
            val stroke = 10.dp.toPx()
            val arcSize = Size(size.width - stroke, size.height - stroke)
            val topLeft = Offset(stroke / 2f, stroke / 2f)
            // Track
            drawArc(
                color = track,
                startAngle = 135f,
                sweepAngle = 270f,
                topLeft = topLeft,
                size = arcSize,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
            // Progress arcs: 12 o'clock to 6 o'clock descending sweep.
            drawArc(
                brush = Brush.sweepGradient(
                    listOf(tint, tint.copy(alpha = 0.5f), tint),
                    center = Offset(size.width / 2f, size.height / 2f),
                ),
                startAngle = 135f,
                sweepAngle = 270f * progress,
                topLeft = topLeft,
                size = arcSize,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round),
            )
        }
        content()
    }
}

@Preview
@Composable
private fun NextPrayerHeroCardPreview() {
    AlAzanTheme {
        NextPrayerHeroCard(
            countdownText = "01:23:45",
            nextPrayerLabel = "Maghrib",
            refreshTick = kotlin.time.Instant.fromEpochMilliseconds(System.currentTimeMillis()),
            elapsedFraction = 0.42f,
        )
    }
}
