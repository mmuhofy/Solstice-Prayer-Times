package com.github.meypod.al_azan.core.domain.model.settings

import android.content.res.Resources
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.github.meypod.al_azan.R
import com.github.meypod.al_azan.core.domain.model.adhan.AdhanKey
import com.github.meypod.al_azan.core.domain.model.adhan.Prayer
import com.github.meypod.al_azan.core.domain.model.alarm.SkippedAlarm
import com.github.meypod.al_azan.core.util.serialization.EmptyStringAsNullSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject

private enum class DefaultAdhanEntryId(
    val key: String,
    val category: AdhanCategory,
) {
    MasjidAnNabawi("masjid_an_nabawi", AdhanCategory.Mosques),
    MasjidAlHaram("masjid_al_haram", AdhanCategory.Mosques),
    MasjidAlAqsa("masjid_al_aqsa", AdhanCategory.Mosques),
    AlAzhar("al_azhar", AdhanCategory.Mosques),
    Suleymaniye("suleymaniye", AdhanCategory.Mosques),
    MasjidAlNabawiOld("masjid_al_nabawi_old", AdhanCategory.Mosques),
    SheikhZayedGrand("sheikh_zayed_grand", AdhanCategory.Mosques),
    HassanIi("hassan_ii", AdhanCategory.Mosques),
    ProphetMosqueMadinah("prophet_mosque_madinah", AdhanCategory.Mosques),
    PutraMosque("putra_mosque", AdhanCategory.Mosques),

    AbdulBasitAbduSamad("abdul_basit_abdus_samad", AdhanCategory.Muezzins),
    RaghebMustafaGhalwash("ragheb_mustafa_ghalwash", AdhanCategory.Muezzins),
    MoazenZade("moazen_zade", AdhanCategory.Muezzins),
    MisharyAlafasy("mishary_alafasy", AdhanCategory.Muezzins),
    AbdulRahmanSudais("abdulrahman_sudais", AdhanCategory.Muezzins),
    SaadAlGhamdi("saad_al_ghamdi", AdhanCategory.Muezzins),
    MasharyRashidAlafasy("mashary_rashid_alafasy", AdhanCategory.Muezzins),
    MaherAlMuaiqly("maher_al_muaiqly", AdhanCategory.Muezzins),
    BandarBalilah("bandar_balilah", AdhanCategory.Muezzins),
    AliAhmedMulla("ali_ahmed_mulla", AdhanCategory.Muezzins),
    MuhammadAlLuhaidan("muhammad_al_luhaidan", AdhanCategory.Muezzins),

    EgyptianStyle("egyptian_style", AdhanCategory.Styles),
    TurkishStyle("turkish_style", AdhanCategory.Styles),
    MoroccanStyle("moroccan_style", AdhanCategory.Styles),
    HijazStyle("hijaz_style", AdhanCategory.Styles),
}

/**
 * Coarse adhan grouping surfaced in the picker UI: mosques (audio captured at famous masjids),
 * muezzins (specific reciters), and styles (regional or stylistic traditions). Stable ids so
 * stored selections round-trip; new entries should pick a stable key and add matching raw assets.
 */
@Serializable
enum class AdhanCategory {
    @SerialName("mosques") Mosques,
    @SerialName("muezzins") Muezzins,
    @SerialName("styles") Styles,
}

/**
 * Stable id of the bundled silent track ([R.raw.silence]). A "muezzin" that plays no audio: selecting it
 * keeps the sound path active (a real, inaudible uri) so vibration/full-screen still work — unlike turning
 * the Sound toggle off, which is notify-only with no vibration. Shared with the reminder silent option.
 */
const val SILENT_AUDIO_ID = "silent"

/**
 * Stable id of the "default notification" muezzin: it has no bundled resource, its uri is resolved
 * dynamically to the system notification sound at fire time (see `AudioEntry.toAudioUri`). The
 * [AudioEntry.ResourceAudioEntry.resId] below is a non-null placeholder only so this round-trips as a
 * bundled (Resource) entry through [AudioEntrySerializer] and reads as resolvable — it is never played.
 */
const val NOTIFICATION_AUDIO_ID = "notification_default"

fun mapAdhanIdToEntry(key: String): AudioEntry.ResourceAudioEntry =
    mapAdhanIdToEntryOrNull(key) ?: mapAdhanIdToEntry(DefaultAdhanEntryId.MasjidAnNabawi.key)

/**
 * Resolves a bundled-sound id to its current resource entry, or `null` for an unknown id. Callers that
 * migrate stored data use this to re-resolve the **current** resource int from the stable string id —
 * the raw resource int is not stable across builds, so a persisted int cannot be trusted.
 */
fun mapAdhanIdToEntryOrNull(key: String): AudioEntry.ResourceAudioEntry? =
    when (key) {
        SILENT_AUDIO_ID -> AudioEntry.ResourceAudioEntry(
            SILENT_AUDIO_ID,
            R.raw.silence,
            R.string.silent_sound,
        )

        NOTIFICATION_AUDIO_ID -> AudioEntry.ResourceAudioEntry(
            NOTIFICATION_AUDIO_ID,
            R.raw.silence,
            R.string.reminder_default_sound,
        )

        DefaultAdhanEntryId.MasjidAnNabawi.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MasjidAnNabawi.key,
            R.raw.masjid_an_nabawi,
            R.string.masjid_an_nabawi,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.AbdulBasitAbduSamad.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.AbdulBasitAbduSamad.key,
            R.raw.abdul_basit_abdus_samad,
            R.string.abdul_basit_abdus_samad,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.RaghebMustafaGhalwash.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.RaghebMustafaGhalwash.key,
            R.raw.ragheb_mustafa_ghalwash,
            R.string.ragheb_mustafa_ghalwash,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.MoazenZade.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MoazenZade.key,
            R.raw.moazen_zade,
            R.string.moazen_zade,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.MasjidAlHaram.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MasjidAlHaram.key,
            R.raw.silence,
            R.string.masjid_al_haram,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.MasjidAlAqsa.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MasjidAlAqsa.key,
            R.raw.silence,
            R.string.masjid_al_aqsa,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.AlAzhar.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.AlAzhar.key,
            R.raw.silence,
            R.string.al_azhar,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.Suleymaniye.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.Suleymaniye.key,
            R.raw.silence,
            R.string.suleymaniye,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.MasjidAlNabawiOld.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MasjidAlNabawiOld.key,
            R.raw.silence,
            R.string.masjid_al_nabawi_old,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.SheikhZayedGrand.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.SheikhZayedGrand.key,
            R.raw.silence,
            R.string.sheikh_zayed_grand,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.HassanIi.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.HassanIi.key,
            R.raw.silence,
            R.string.hassan_ii,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.ProphetMosqueMadinah.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.ProphetMosqueMadinah.key,
            R.raw.silence,
            R.string.prophet_mosque_madinah,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.PutraMosque.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.PutraMosque.key,
            R.raw.silence,
            R.string.putra_mosque,
            AdhanCategory.Mosques,
        )

        DefaultAdhanEntryId.MisharyAlafasy.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MisharyAlafasy.key,
            R.raw.silence,
            R.string.mishary_alafasy,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.AbdulRahmanSudais.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.AbdulRahmanSudais.key,
            R.raw.silence,
            R.string.abdulrahman_sudais,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.SaadAlGhamdi.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.SaadAlGhamdi.key,
            R.raw.silence,
            R.string.saad_al_ghamdi,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.MasharyRashidAlafasy.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MasharyRashidAlafasy.key,
            R.raw.silence,
            R.string.mashary_rashid_alafasy,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.MaherAlMuaiqly.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MaherAlMuaiqly.key,
            R.raw.silence,
            R.string.maher_al_muaiqly,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.BandarBalilah.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.BandarBalilah.key,
            R.raw.silence,
            R.string.bandar_balilah,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.AliAhmedMulla.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.AliAhmedMulla.key,
            R.raw.silence,
            R.string.ali_ahmed_mulla,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.MuhammadAlLuhaidan.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MuhammadAlLuhaidan.key,
            R.raw.silence,
            R.string.muhammad_al_luhaidan,
            AdhanCategory.Muezzins,
        )

        DefaultAdhanEntryId.EgyptianStyle.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.EgyptianStyle.key,
            R.raw.silence,
            R.string.egyptian_style,
            AdhanCategory.Styles,
        )

        DefaultAdhanEntryId.TurkishStyle.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.TurkishStyle.key,
            R.raw.silence,
            R.string.turkish_style,
            AdhanCategory.Styles,
        )

        DefaultAdhanEntryId.MoroccanStyle.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.MoroccanStyle.key,
            R.raw.silence,
            R.string.moroccan_style,
            AdhanCategory.Styles,
        )

        DefaultAdhanEntryId.HijazStyle.key -> AudioEntry.ResourceAudioEntry(
            DefaultAdhanEntryId.HijazStyle.key,
            R.raw.silence,
            R.string.hijaz_style,
            AdhanCategory.Styles,
        )

        else -> null
    }

fun getDefaultAdhanEntries(): List<AudioEntry.ResourceAudioEntry> =
    DefaultAdhanEntryId.entries.map { mapAdhanIdToEntry(it.key) }

@Serializable
data class Settings(
    val deliveredAlarmTimestamps: Map<String, Long?> = emptyMap(),
    val themeColor: ThemeColor = ThemeColor.Default,
    /**
     * Optional user-picked ARGB seed color applied on top of [themeColor] when it equals
     * [ThemeColor.Dynamic] (Material You override) or [ThemeColor.Default] (pre-Android 12 fallback).
     * Null disables the custom seed; on Android 12+ the OS dynamic scheme wins unless this is set.
     */
    val customSeedColor: Int? = null,
    val is24HourFormat: Boolean = true,
    /** App-wide UI scale multiplier (text + interface) applied on top of the system density. 1f = no change. */
    val displayScale: Float = 1f,
    val numberingSystem: NumberingSystem = NumberingSystem.Default,
    val highlightCurrentPrayer: Boolean = false,
    val selectedLocale: String,
    val selectedArabicCalendar: String = "islamic",
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val selectedLocaleForArabicCalendar: String? = null,
    val selectedSecondaryCalendar: SecondaryCalendar = SecondaryCalendar.Gregorian,
    /** Hide the date shown in the home top app bar (next to the menu button). */
    val hideToolbarCalendar: Boolean = false,
    /** Swap home calendar positions: secondary calendar in the header, lunar (Hijri) date in the toolbar. */
    val swapHomeCalendars: Boolean = false,
    val appInitialConfigDone: Boolean = false,
    val appIntroDone: Boolean = false,
    val savedAdhanAudioEntries: List<AudioEntry> = getDefaultAdhanEntries(),
    val savedUserAudioEntries: List<AudioEntry.ExternalAudioEntry> = emptyList(),
    val selectedAdhanEntries: Map<AdhanKey, AudioEntry> = mapOf(
        AdhanKey.Default to getDefaultAdhanEntries()[0],
    ),
    val lastAppFocusTimestamp: Int? = null,
    val hiddenPrayers: List<Prayer> = emptyList(),
    val adhanVolume: Int? = null,
    val volumeButtonStopsAdhan: Boolean = false,
    val preferExternalAudioDevice: Boolean = false,
    val bypassDnd: Boolean = false,
    val hiddenWidgetPrayers: List<Prayer> = listOf(Prayer.Sunset, Prayer.Midnight, Prayer.Tahajjud),
    val showWidget: Boolean = false,
    /** Layout the notification widget renders in: the full prayer table or the compact next-prayer view. */
    val notificationWidgetLayout: NotificationWidgetLayout = NotificationWidgetLayout.Table,
    val showWidgetCountdown: Boolean = false,
    val adaptiveWidgets: Boolean = false,
    val widgetCityNamePos: WidgetCityNamePos = WidgetCityNamePos.None,
    val swapWidgetLayoutDirection: Boolean = false,
    /** Widget-only counterpart of [highlightCurrentPrayer]: highlight the current prayer in the widgets. */
    val highlightCurrentPrayerWidget: Boolean = false,
    /** Advance the widget's Hijri date by a day after maghrib (the Islamic day starts at sunset). */
    val widgetHijriDayStartsAtMaghrib: Boolean = false,
    /** Prayers the 1x1 next-prayer widget's countdown counts toward. */
    val countdownWidgetPrayers: List<Prayer> = listOf(
        Prayer.Fajr,
        Prayer.Dhuhr,
        Prayer.Asr,
        Prayer.Maghrib,
        Prayer.Isha,
    ),
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val calcSettingsHash: String? = null,
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val alarmSettingsHash: String? = null,
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val reminderSettingsHash: String? = null,
    val isPlayingAudio: Boolean = false,
    val dontAskPermissionNotifications: Boolean = false,
    val dontAskPermissionAlarm: Boolean = false,
    val dontAskPermissionPhoneState: Boolean = false,
    val dontAskPermissionFullScreenIntent: Boolean = false,
    val dontAskPermissionDndAccess: Boolean = false,
    val dontAskPermissionBatteryOptimization: Boolean = false,
    val devMode: Boolean = false,
    val qiblaFinderUnderstood: Boolean = false,
    val qiblaFinderOrientationLocked: Boolean = true,
    val counterHistoryVisible: Boolean = false,
    val upcomingAlarmsHelpDismissed: Boolean = false,
    val advancedCustomAdhan: Boolean = false,
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val ramadanRemindedYear: String? = null,
    val ramadanReminderDontShow: Boolean = false,
    val useDifferentAlarmType: Boolean = false,
    val hijriMonthlyView: Boolean = false,
    @Serializable(with = EmptyStringAsNullSerializer::class)
    val locationIdBeforeTravel: String? = null,
    val travelModeLastUpdateMillis: Long? = null,
    val showHomeNextPrayerCountdown: Boolean = true,
    val countdownSkipNonPrayers: Boolean = false,
    /**
     * Shortcut icon buttons shown in the home top app bar, in the user's chosen left-to-right order,
     * capped at [MAX_HOME_SHORTCUTS].
     */
    val homeShortcuts: List<HomeShortcut> = emptyList(),
    /**
     * Occurrences the user skipped from the Scheduled-alarms screen, keyed logically (prayer/reminder +
     * date). Schedulers exclude a matching occurrence when picking what to arm, so the *following* one
     * fires instead, and prune their own past-day entries on every run. See
     * [com.github.meypod.al_azan.core.domain.model.alarm.SkippedAlarm].
     *
     * Renamed from the legacy `skippedAlarms` (timestamp-keyed): legacy entries decode as an ignored
     * unknown key and are dropped — harmless, since skips are ephemeral future-only state.
     */
    val skippedOccurrences: List<SkippedAlarm> = emptyList(),
    /** Epoch millis until which alarms (adhan and reminders) are suppressed ("Dismiss & silent"). Null = not silenced. */
    val silencedUntilMillis: Long? = null,
    /**
     * Interruption filter captured before the "Dismiss & silent" DND window, restored when it ends.
     * Null = nothing to restore.
     */
    val dndRestoreFilter: Int? = null,
    /**
     * Always launch the full-screen alarm activity when an alarm sounds, instead of relying on the
     * notification's full-screen-intent. Some users prefer it, and some OEMs don't honor the full-screen
     * intent over the lock screen. Only takes effect when the full-screen alarm is enabled.
     */
    val forceLaunchAlarmActivity: Boolean = false,
)

@Serializable
enum class NumberingSystem(
    val value: String,
) {
    @SerialName("")
    Default(""),

    @SerialName("latn")
    Latn("latn"),

    @SerialName("arab")
    Arab("arab"),

    @SerialName("arabext")
    Arabext("arabext"),
}

@Serializable
enum class SecondaryCalendar(
    val value: String,
) {
    @SerialName("gregorian")
    Gregorian("gregorian"),

    @SerialName("persian")
    Persian("persian"),

    @SerialName("ethiopic")
    Ethiopic("ethiopic"),

    @SerialName("buddhist")
    Buddhist("buddhist"),
}

@Serializable
enum class WidgetCityNamePos {
    @SerialName("")
    None,

    @SerialName("top_start")
    TopStart,

    @SerialName("top_end")
    TopEnd,
}

fun WidgetCityNamePos.i18n(resources: Resources): String =
    when (this) {
        WidgetCityNamePos.None -> resources.getString(R.string.none)
        WidgetCityNamePos.TopStart -> resources.getString(R.string.lunar_calendar)
        WidgetCityNamePos.TopEnd -> resources.getString(R.string.secondary_calendar)
    }

@Serializable
enum class NotificationWidgetLayout {
    @SerialName("table")
    Table,

    @SerialName("compact")
    Compact,

    @SerialName("custom")
    Custom,
}

fun NotificationWidgetLayout.i18n(resources: Resources): String =
    when (this) {
        NotificationWidgetLayout.Table -> resources.getString(R.string.notification_widget_layout_table)
        NotificationWidgetLayout.Compact -> resources.getString(R.string.notification_widget_layout_compact)
        NotificationWidgetLayout.Custom -> resources.getString(R.string.widget_section_custom_appearance)
    }

@Serializable
enum class ThemeColor {
    @SerialName("default")
    Default,

    @SerialName("light")
    Light,

    @SerialName("dark")
    Dark,

    @SerialName("amoled")
    Amoled,

    @SerialName("classic_light")
    ClassicLight,

    @SerialName("classic_dark")
    ClassicDark,

    @SerialName("dynamic")
    Dynamic,

    ;

    fun isClassic(): Boolean =
        when (this) {
            ClassicLight, ClassicDark -> true
            else -> false
        }

    fun isAmoled(): Boolean = this == Amoled

    @Composable
    fun isDark(): Boolean =
        when (this) {
            ClassicDark, Dark, Amoled -> true
            Dynamic -> isSystemInDarkTheme()
            else -> false
        }
}

@Serializable(with = AudioEntrySerializer::class)
sealed interface AudioEntry {
    val id: String
    val loop: Boolean

    @Serializable
    data class ResourceAudioEntry(
        override val id: String,
        @param:RawRes val resId: Int? = null,
        @param:StringRes val labelResId: Int = R.string.unknown,
        /**
         * Coarse grouping surfaced in the picker UI; null for the synthetic silent/notification
         * entries. Category-based sorting/grouping keeps the picker usable as the bundled catalog
         * grows beyond a dozen reciters.
         */
        val category: AdhanCategory? = null,
    ) : AudioEntry {
        override val loop: Boolean = false
        val canDelete: Boolean = false
    }

    @Serializable
    data class ExternalAudioEntry(
        override val id: String,
        @Serializable(with = EmptyStringAsNullSerializer::class) val filepath: String? = null,
        val label: String = "",
        override val loop: Boolean = false,
    ) : AudioEntry {
        val canDelete: Boolean = true
    }

    @Composable
    fun getLabel(): String =
        when (this) {
            is ResourceAudioEntry -> stringResource(this.labelResId)
            is ExternalAudioEntry -> this.label
        }
}

/**
 * Picks the [AdhanCategory] of a bundled entry, or null for the silent/notification entries and
 * user-imported external audio. Lets the adhan picker group user-imported tracks under a single
 * "Custom" bucket alongside the bundled mosques/muezzins/styles while still letting the silent
 * notification entry render ungrouped.
 */
fun AudioEntry.getCategory(): AdhanCategory? =
    when (this) {
        is AudioEntry.ResourceAudioEntry -> this.category
        is AudioEntry.ExternalAudioEntry -> null
    }

/**
 * Whether this entry can actually resolve to a playable sound. A [AudioEntry.ResourceAudioEntry] with
 * a null [AudioEntry.ResourceAudioEntry.resId], or an [AudioEntry.ExternalAudioEntry] with a null path,
 * is a broken/orphaned selection (a deleted sound, or data corrupted by an older build) — callers
 * should fall back to the global default rather than show "unknown" or play nothing.
 */
fun AudioEntry.isResolvable(): Boolean =
    when (this) {
        is AudioEntry.ResourceAudioEntry -> resId != null
        is AudioEntry.ExternalAudioEntry -> !filepath.isNullOrEmpty()
    }

object AudioEntrySerializer :
    JsonContentPolymorphicSerializer<AudioEntry>(AudioEntry::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<AudioEntry> {
        // Bundled sounds persist a non-null `resId`; external (device/user) sounds never do. The
        // discriminator must pick the matching deserializer — using ResourceAudioEntry for everything
        // strips the filepath/label off external entries and leaves them resolving to "unknown".
        val hasResId = element.jsonObject["resId"]?.takeIf { it != JsonNull } != null
        return if (hasResId) {
            ReResolvingResourceAudioEntrySerializer
        } else {
            AudioEntry.ExternalAudioEntry.serializer()
        }
    }
}

/**
 * Persisted [AudioEntry.ResourceAudioEntry.resId]/[AudioEntry.ResourceAudioEntry.labelResId] are raw
 * `R` ints, which are **not stable across builds** — a value saved by an older build can point to a
 * different resource or to none at all, crashing with `Resources$NotFoundException` when the label is
 * resolved. On read, re-resolve the entry from its stable string [AudioEntry.id]; an id no longer
 * bundled degrades to an unresolvable entry ("unknown" label, null sound) instead of crashing.
 */
private object ReResolvingResourceAudioEntrySerializer : KSerializer<AudioEntry.ResourceAudioEntry> {
    private val delegate = AudioEntry.ResourceAudioEntry.serializer()
    override val descriptor = delegate.descriptor

    override fun serialize(
        encoder: Encoder,
        value: AudioEntry.ResourceAudioEntry,
    ) = delegate.serialize(encoder, value)

    override fun deserialize(decoder: Decoder): AudioEntry.ResourceAudioEntry {
        val decoded = delegate.deserialize(decoder)
        return mapAdhanIdToEntryOrNull(decoded.id)
            ?: decoded.copy(resId = null, labelResId = R.string.unknown)
    }
}
