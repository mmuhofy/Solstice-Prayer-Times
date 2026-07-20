package com.github.meypod.al_azan

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.github.meypod.al_azan.core.domain.model.settings.Settings
import com.github.meypod.al_azan.core.domain.repository.SettingsRepository
import com.github.meypod.al_azan.core.presentation.AlAzanTheme
import com.github.meypod.al_azan.core.presentation.navigation.NavigationController
import com.github.meypod.al_azan.core.presentation.navigation.NavigationRoot
import com.github.meypod.al_azan.core.presentation.navigation.Route
import com.github.meypod.al_azan.core.presentation.navigation.deepLinkPatterns
import com.github.meypod.al_azan.core.presentation.navigation.deeplink.parseUriToRoute
import com.github.meypod.al_azan.di.LanguageSync
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    @Inject
    lateinit var languageSync: LanguageSync

    // Holds the settings loaded during onCreate; null while the background load is in flight.
    // Observed by setContent via mutableStateOf, so the UI renders once data is ready.
    private var initialSettings: Settings? by mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Keep the splash screen visible until the initial settings load completes on the IO
        // dispatcher; replaces runBlocking so the main thread is not blocked during launch.
        splashScreen.setKeepOnScreenCondition { initialSettings == null }

        val startingRoute = routeFromIntent(intent)
        intent = null // consume

        // Reconcile the app locale with stored settings before composing: applies the migrated/
        // selected language and its layout direction (RTL). Done here (not Application.onCreate) so
        // it lands at the lifecycle point where autoStoreLocales actually persists
        // setApplicationLocales. Run on IO to avoid blocking the main thread.
        lifecycleScope.launch {
            val settings = withContext(Dispatchers.IO) {
                languageSync.reconcile()
                settingsRepository.fetch()
            }
            initialSettings = settings
        }

        setContent {
            val initial = initialSettings ?: return@setContent
            val settings by settingsRepository.data.collectAsState(initial = initial)

            AlAzanTheme(settings.themeColor, settings.displayScale, settings.customSeedColor) {
                NavigationRoot(
                    appIntroDone = initial.appIntroDone,
                    startingRoute = startingRoute,
                )
            }
        }
    }

    // A deep link / DND-rule tap that arrives while the Activity is already running comes here (the
    // PendingIntent is SINGLE_TOP | CLEAR_TOP), not through onCreate — route it onto the live backstack.
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        routeFromIntent(intent)?.let { NavigationController.navigateTo(it) }
    }

    // MainActivity is exported, so the URI is attacker-reachable; a malformed deep link must not crash
    // launch. Fall back to the default start destination on any parse failure.
    private fun routeFromIntent(launchIntent: Intent?): Route? =
        launchIntent?.data?.let { runCatching { parseUriToRoute(it, deepLinkPatterns) }.getOrNull() }
}
