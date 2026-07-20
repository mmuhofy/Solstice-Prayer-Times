package com.github.meypod.al_azan.core.presentation.navigation

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable

/**
 * Intercept a predictive back gesture before the system pops the current destination. Use this
 * for actions that should be undone by the same swipe — closing a sheet/dialog/snackbar, undoing a
 * pending choice — before the fallback (pop or finish) fires.
 *
 * The lib [activity-compose] PredictiveBackHandler routes the gesture offset to [progress] so the
 * UI can react live (e.g. animate a drawer closing). When the gesture completes press-back this
 * calls [onBack]; the system then proceeds with its default (pop the navigator).
 */
@Composable
fun PredictableBack(
    enabled: Boolean,
    onBack: () -> Unit,
    onProgress: ((Float) -> Unit)? = null,
) {
    PredictiveBackHandler(enabled = enabled) { backEvent ->
        if (onProgress != null) {
            // Streamed progress until completion (Android 14+); ignored pre-34.
            try {
                backEvent.collect { onProgress(it.progress) }
            } catch (_: Throwable) {
                // Some emulator / older devices may throw on collect; fall through to onBack.
            }
        }
        onBack()
    }
}
