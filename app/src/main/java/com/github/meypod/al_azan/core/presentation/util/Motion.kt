package com.github.meypod.al_azan.core.presentation.util

import android.provider.Settings
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Returns true when the OS reports the user has either enabled a system-wide "reduce animations"
 * preference or turned off transition animations. Surfaced through [Settings.Global.ANIMATOR_DURATION_SCALE]
 * (a value of 0 means "animations disabled" — used by the system since API 17).
 *
 * Compose-side helpers use this to short-circuit long animations so users who have requested
 * reduced motion aren't subjected to e.g. progress arcs or slide+fade navigation.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) { isReducedMotion(context) }
}

private fun isReducedMotion(context: android.content.Context): Boolean {
    val scale = Settings.Global.getFloat(
        context.contentResolver,
        Settings.Global.ANIMATOR_DURATION_SCALE,
        1f,
    )
    // 0 means animations are off system-wide; anything < 0.5 means significantly slowed.
    return scale < 0.5f
}

/** Replace any animation spec with an "instant" one when motion is reduced; otherwise pass through. */
@Composable
fun AnimationSpec<*>.reduceMotionIfNeeded(
    reducedMotion: Boolean = rememberReducedMotion(),
): AnimationSpec<*> =
    if (reducedMotion) tween(durationMillis = 1, easing = FastOutSlowInEasing) else this

/**
 * Helper: returns the [androidx.compose.animation.core.tween] parameters for a navigation transition
 * that shrink the duration when the user has reduced motion enabled.
 */
@Composable
fun rememberNavAnimations(reducedMotion: Boolean = rememberReducedMotion()): NavAnimations =
    NavAnimations(if (reducedMotion) 60 else 320, if (reducedMotion) 30 else 160)

data class NavAnimations(val durationMillis: Int, val fadeMillis: Int)
