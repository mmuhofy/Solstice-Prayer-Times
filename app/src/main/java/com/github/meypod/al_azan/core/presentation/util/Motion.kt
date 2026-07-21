package com.github.meypod.al_azan.core.presentation.util

import android.app.AccessibilityManager
import android.content.Context
import android.os.Build
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

/**
 * Returns true when the OS reports the user has either enabled a system-wide "reduce animations"
 * preference (Android 9+ via [AccessibilityManager.areAnimationsDisabled]) or turned off all
 * transition animations at the system level (Android 11+ via
 * [AccessibilityManager.isAnimationOn]).
 *
 * Used to swap build-flagged animations for instant or faster transitions so users who have
 * requested reduced motion aren't subjected to e.g. progress arcs or slide+fade navigation.
 */
@Composable
fun rememberReducedMotion(): Boolean {
    val context = LocalContext.current
    return remember(context) { isReducedMotion(context) }
}

private fun isReducedMotion(context: Context): Boolean {
    val am = context.applicationContext.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        ?: return false
    // Available since Android 9 (Q): user has explicitly disabled animation globally.
    if (am.areAnimationsDisabled()) return true
    // Available since Android 11: explicit "animation off" preference. When false, motion is
    // allowed; when true, motion is suppressed and we should shrink durations.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !am.isAnimationOn()) return true
    return false
}

/** Replace any animation spec with an "instant" one when motion is reduced; otherwise pass through. */
@Composable
fun <T, V : AnimationVector> AnimationSpec<T>.reduceMotionIfNeeded(
    reducedMotion: Boolean = rememberReducedMotion(),
): AnimationSpec<T> =
    if (reducedMotion) tween(durationMillis = 1, easing = FastOutSlowInEasing) else this

/**
 * Helper: returns the [androidx.compose.animation.core.tween] parameters for a navigation transition
 * that shrink the duration when the user has reduced motion enabled.
 */
@Composable
fun rememberNavAnimations(reducedMotion: Boolean = rememberReducedMotion()): NavAnimations =
    NavAnimations(if (reducedMotion) 60 else 320, if (reducedMotion) 30 else 160)

data class NavAnimations(val durationMillis: Int, val fadeMillis: Int)
