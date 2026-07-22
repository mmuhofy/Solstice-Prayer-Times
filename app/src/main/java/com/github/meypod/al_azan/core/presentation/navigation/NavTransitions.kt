package com.github.meypod.al_azan.core.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
 * M3 Expressive-style eased slide + fade pair. The fade is half the slide duration so the
 * destination doesn't flash on entry.
 */
private const val NAV_ANIM_MS = 320
private const val NAV_FADE_MS = 160

private fun slideFadePair(
    enterOffset: (Int) -> Int,
    exitOffset: (Int) -> Int,
): Pair<EnterTransition, ExitTransition> = Pair(
    fadeIn(animationSpec = tween(NAV_FADE_MS, easing = FastOutSlowInEasing)) +
        slideInHorizontally(animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing)) {
            fw -> enterOffset(fw)
        },
    fadeOut(animationSpec = tween(NAV_FADE_MS, easing = FastOutSlowInEasing)) +
        slideOutHorizontally(animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing)) {
            fw -> exitOffset(fw)
        },
)

/**
 * Pre-built transition lambdas for [androidx.navigation3.ui.NavDisplay]. Each returning lambda is
 * typed as `AnimatedContentTransitionScope<*>.() -> ContentTransform` (the most general shape that
 * satisfies Navigation 3's `transitionSpec` while matching the 1-arg + 2-arg overload family).
 *
 * Compose Compiler derives a `ComposableFunction1`/`ComposableFunction2` shape; we keep the documented
 * (Int) -> ContentTransform variant for predictive-pop explicitly so overload resolution wins for
 * the 2-arg call site.
 */
class NavTransitionSpecs internal constructor(
    private val forwardEnter: (Int) -> Int,
    private val forwardExit: (Int) -> Int,
    private val popEnter: (Int) -> Int,
    private val popExit: (Int) -> Int,
    private val predictivePopEnter: (Int) -> Int,
    private val predictivePopExit: (Int) -> Int,
) {
    fun forwardTransform(
    ): AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        val (incoming, outgoing) = slideFadePair(forwardEnter, forwardExit)
        incoming togetherWith outgoing
    }

    fun popTransform(
    ): AnimatedContentTransitionScope<*>.() -> ContentTransform = {
        val (incoming, outgoing) = slideFadePair(popEnter, popExit)
        incoming togetherWith outgoing
    }

    fun predictivePopTransform(
    ): AnimatedContentTransitionScope<*>.(Int) -> ContentTransform = { _ ->
        val (incoming, outgoing) = slideFadePair(predictivePopEnter, predictivePopExit)
        incoming togetherWith outgoing
    }
}

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
