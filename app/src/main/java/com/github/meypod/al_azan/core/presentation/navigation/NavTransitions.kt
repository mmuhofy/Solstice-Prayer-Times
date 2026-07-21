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
 * M3 Expressive-style eased slide + fade transitions for Navigation 3's [animatedContentTransitionSpec]
 * -shaped lambdas. Built once with [rememberNavTransitionSpecs] and reused at every call site, so the
 * NavDisplay arguments resolve directly against the qualifier inputs without per-call work.
 */
private const val NAV_ANIM_MS = 320
private const val NAV_FADE_MS = 160

private fun slideFade(
    enterOffset: (Int) -> Int,
    exitOffset: (Int) -> Int,
): Pair<EnterTransition, ExitTransition> = Pair(
    fadeIn(animationSpec = tween(NAV_FADE_MS, easing = FastOutSlowInEasing)) +
        slideInHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
        ) { fw -> enterOffset(fw) },
    fadeOut(animationSpec = tween(NAV_FADE_MS, easing = FastOutSlowInEasing)) +
        slideOutHorizontally(
            animationSpec = tween(NAV_ANIM_MS, easing = FastOutSlowInEasing),
        ) { fw -> exitOffset(fw) },
)

/**
 * Pre-built transition lambdas for [androidx.navigation3.ui.NavDisplay]. Each entry exposes itself as
 * the shape NavDisplay expects: a [AnimatedContentTransitionScope]-receiver function returning a
 * [ContentTransform]. The Navigation 3 spec infers its concrete generic parameter from the receiver
 * context (which is encoded inside the lambda); we don't print it here — the lambda only captures
 * the navigation directions at construction time.
 */
class NavTransitionSpecs internal constructor(
    private val forwardEnter: (Int) -> Int,
    private val forwardExit: (Int) -> Int,
    private val popEnter: (Int) -> Int,
    private val popExit: (Int) -> Int,
    private val predictivePopEnter: (Int) -> Int,
    private val predictivePopExit: (Int) -> Int,
) {
    /**
     * Produces a generic-shaped lambda of the form `AnimatedContentTransitionScope<*>.(...) -> ContentTransform`.
     * We use an explicit unchecked cast at the boundary because NavDisplay's expected shape is
     * internal-API and not public-on-every-type — calling sites have historically trusted
     * NaivePair approach to satisfying the receiver. Cleaner updates may follow when Compose 1.10
     * exposes a public qualifier.
     */
    @Suppress("UNCHECKED_CAST")
    fun forwardTransform(): Any = {
        val (incoming, outgoing) = slideFade(forwardEnter, forwardExit)
        incoming togetherWith outgoing
    }

    @Suppress("UNCHECKED_CAST")
    fun popTransform(): Any = {
        val (incoming, outgoing) = slideFade(popEnter, popExit)
        incoming togetherWith outgoing
    }

    @Suppress("UNCHECKED_CAST")
    fun predictivePopTransform(): Any = {
        val (incoming, outgoing) = slideFade(predictivePopEnter, predictivePopExit)
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
