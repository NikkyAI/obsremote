package moe.nikky.vrcobs

import kotlinx.datetime.Instant
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

val Duration.withPrecisionSeconds get() = toComponents { seconds, nano ->
    ((nano / 1_000_000_000f).roundToInt() + seconds).seconds
}
val Duration.withPrecisionMilliseconds get() = (this * 1000.0 ).withPrecisionSeconds / 1000.0
val Duration.withPrecisionMicroSeconds get() = (this * 1000_000.0 ).withPrecisionSeconds / 1000_000.0


val Duration.withPrecisionMinutes get() = toComponents { minutes, seconds, nano ->
    minutes.minutes
}
val Duration.withPrecisionHours get() = toComponents { hours, minutes, seconds, nano ->
    hours.hours
}

val Instant.withPrecisionMilliseconds get() = Instant.fromEpochMilliseconds(toEpochMilliseconds())
val Instant.withPrecisionSeconds get() = Instant.fromEpochSeconds(toEpochMilliseconds() / 1000)