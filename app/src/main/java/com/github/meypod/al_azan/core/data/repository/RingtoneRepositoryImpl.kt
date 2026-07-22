package com.github.meypod.al_azan.core.data.repository

import android.content.Context
import android.media.RingtoneManager
import com.github.meypod.al_azan.core.domain.model.audio.DeviceRingtone
import com.github.meypod.al_azan.core.domain.repository.RingtoneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Enumerates system sounds via [RingtoneManager], mirroring the old app's `getRingtones`: notification,
 * alarm, and ringtone sounds. Each distinct sound is offered in two variants — play-once and looping
 * ("Repeat") — so a reminder can either chime once or keep ringing. Duplicate uris are collapsed first.
 */
class RingtoneRepositoryImpl(
    private val context: Context,
) : RingtoneRepository {
    private data class BaseSound(
        val id: String,
        val label: String,
        val uri: String,
    )

    override suspend fun getDeviceRingtones(): List<DeviceRingtone> =
        withContext(Dispatchers.IO) {
            val seen = HashSet<String>()
            // Local lambda so the shared dedup `seen` set is captured lexically, keeping
            // MutableSet out of any function signature (banMutableCollectionTypes lint rule).
            fun collectType(type: Int): ArrayList<BaseSound> {
                val out = ArrayList<BaseSound>()
                try {
                    val manager = RingtoneManager(context).apply { setType(type) }
                    val cursor = manager.cursor
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(RingtoneManager.ID_COLUMN_INDEX).toString()
                        val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX) ?: continue
                        val uri = manager.getRingtoneUri(cursor.position)?.toString() ?: continue
                        if (!seen.add(uri)) continue
                        out.add(BaseSound(id = "ringtone_$id", label = title, uri = uri))
                    }
                } catch (_: Exception) {
                    // A locked-down OEM or storage error shouldn't break the picker; skip this type.
                }
                return out
            }
            val base = buildList {
                addAll(collectType(RingtoneManager.TYPE_NOTIFICATION))
                addAll(collectType(RingtoneManager.TYPE_ALARM))
                addAll(collectType(RingtoneManager.TYPE_RINGTONE))
            }
            base.flatMap { sound ->
                listOf(
                    DeviceRingtone(id = sound.id, label = sound.label, uri = sound.uri, loop = false),
                    DeviceRingtone(id = "${sound.id}_loop", label = sound.label, uri = sound.uri, loop = true),
                )
            }
        }
}
