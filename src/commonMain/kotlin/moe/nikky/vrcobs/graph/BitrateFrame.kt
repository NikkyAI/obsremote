package moe.nikky.vrcobs.graph

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BitrateFrame(
    val timestamp: Long,
    val bitrate: Long
) {
    val instant: Instant get() = Instant.fromEpochMilliseconds(timestamp)
}