package com.github.meypod.al_azan.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

data class HorizontalSlideDirections(
    val forwardEnter: Int,
    val forwardExit: Int,
    val backEnter: Int,
    val backExit: Int,
)

@Composable
fun rememberHorizontalSlideDirections(): HorizontalSlideDirections {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    return remember(isRtl) {
        val forwardEnterDirection = if (isRtl) -1 else 1
        val forwardExitDirection = -forwardEnterDirection
        HorizontalSlideDirections(
            forwardEnter = forwardEnterDirection,
            forwardExit = forwardExitDirection,
            backEnter = forwardExitDirection,
            backExit = forwardEnterDirection,
        )
    }
}

/**
 * M3 Expressive-style eased slide + fade ContentTransform. Caller passes per-direction offsets
 * derived from [HorizontalSlideDirections]. The fade is half the slide duration so the surface
 * doesn't flash on entry.
 */
private const val NAV_ANIM_MS = 320
private const val NAV_FADE_MS = 160

fun AnimatedContentTransitionScope<Any?>.softFadeSlide(
    enterOffset: (Int) -> Int,
    exitOffset: (Int) -> Int,
): ContentTransform {
    val enterEasing = FastOutSlowInEasing
    val exitEasing = FastOutSlowInEasing
    return fadeIn(animationSpec = tween(NAV_FADE_MS, easing = enterEasing)) +
        androidx.compose.animation.slideInHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = enterEasing),
        ) { fw -> enterOffset(fw) } togetherWith
        fadeOut(animationSpec = tween(NAV_FADE_MS, easing = exitEasing)) +
        androidx.compose.animation.slideOutHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = exitEasing),
        ) { fw -> exitOffset(fw) }
}

/**
 * Pre-built transition lambdas for [androidx.navigation3.ui.NavDisplay]. All three share the same
 * eased slide+fade combo so the transition feels uniform in either direction; predictive-pop offsets
 * the exit further so the prior destination stays visible underneath the user's gesture.
 */
@Composable
fun rememberNavTransitionSpecs(): NavTransitionSpecs {
    val dirs = rememberHorizontalSlideDirections()
    return remember(dirs) {
        NavTransitionSpecs(
            forwardEnter = { fw -> fw * dirs.forwardEnter / 4 },
            forwardExit = { fw -> fw * dirs.forwardExit / 2 },
            popEnter = { fw -> fw * dirs.backEnter / 4 },
            popExit = { fw -> fw * dirs.backExit / 3 },
            predictivePopEnter = { fw -> fw * dirs.backEnter / 4 },
            predictivePopExit = { fw -> fw * dirs.backExit / 3 },
        )
    }
}

class NavTransitionSpecs internal constructor(
    val forwardEnter: (Int) -> Int,
    val forwardExit: (Int) -> Int,
    val popEnter: (Int) -> Int,
    val popExit: (Int) -> Int,
    val predictivePopEnter: (Int) -> Int,
    val predictivePopExit: (Int) -> Int,
) {
    fun forwardTransform(): AnimatedContentTransitionScope<Any?>.() -> ContentTransform =
        { softFadeSlide(forwardEnter, forwardExit) }

    fun popTransform(): AnimatedContentTransitionScope<Any?>.() -> ContentTransform =
        { softFadeSlide(popEnter, popExit) }

    fun predictivePopTransform(): AnimatedContentTransitionScope<Any?>.() -> ContentTransform =
        { softFadeSlide(predictivePopEnter, predictivePopExit) }
}
