package com.github.meypod.al_azan.main.home

import androidx.compose.runtime.Immutable
import com.github.meypod.al_azan.core.domain.model.adhan.Prayer
import com.github.meypod.al_azan.core.domain.model.adhan.ShariaTimes
import com.github.meypod.al_azan.core.domain.model.favorite_location.FavoriteLocation
import com.github.meypod.al_azan.core.domain.model.settings.HomeShortcut
import com.github.meypod.al_azan.core.domain.model.settings.NumberingSystem
import com.github.meypod.al_azan.core.domain.model.settings.ThemeColor
import com.github.meypod.al_azan.core.domain.usecase.ShariaTimeDetails
import kotlin.time.Instant

@Immutable
data class HomeUiState(
    val themeColor: ThemeColor = ThemeColor.Default,
    val currentInstant: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
    val viewingInstant: Instant = Instant.fromEpochMilliseconds(System.currentTimeMillis()),
    val arabicCalendar: String = "islamic",
    val arabicCalendarLocale: String = "en-US",
    val hijriDateAdjustment: Int = 0,
    val calendar: String = "gregorian",
    val locale: String = "en-US",
    val numberingSystem: NumberingSystem = NumberingSystem.Default,
    val location: FavoriteLocation? = null,
    val isCalculationConfigured: Boolean = false,
    val showNextPrayerCountdown: Boolean = true,
    val shariaTimes: ShariaTimes? = null,
    val nextShariaTime: ShariaTimeDetails? = null,
    val highlightedShariaTime: ShariaTimeDetails? = null,
    val countdownText: String = "--:--:--",
    val is24Hour: Boolean = true,
    /** Fraction of the current interval between the previous and next prayer (0..1) for the
     *  hero progress arc. 0 = current prayer just begun, 1 = next prayer is imminently due. */
    val elapsedFractionOfPrayerInterval: Float = 0f,
    val hiddenPrayers: List<Prayer> = emptyList(),
    val skippedPrayers: Set<Prayer> = emptySet(),
    val isDeveloper: Boolean = false,
    val homeShortcuts: List<HomeShortcut> = emptyList(),
    val hideToolbarCalendar: Boolean = false,
    val swapHomeCalendars: Boolean = false,
)
