package moe.nikky.vrcobs.graph

import kotlin.time.Instant
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable
data class BitrateFrame(
    val timestamp: Long,
    val bitrate: Long
) {
    @OptIn(ExperimentalTime::class)
    val instant: Instant get() = Instant.fromEpochMilliseconds(timestamp)
}