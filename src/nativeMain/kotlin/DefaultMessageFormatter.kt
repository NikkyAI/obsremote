import io.github.oshai.kotlinlogging.Formatter
import io.github.oshai.kotlinlogging.KLoggingEvent
import io.github.oshai.kotlinlogging.Level

class DefaultMessageFormatter(private val includePrefix: Boolean = true) : Formatter {
  override fun formatMessage(loggingEvent: KLoggingEvent): String {
    with(loggingEvent) {
      return buildString {
        append(prefix(level, loggerName))
        marker?.getName()?.let {
          append(it)
          append(" ")
        }
        append(message)
        append(cause.throwableToString())
      }
    }
  }

  private fun prefix(level: Level, loggerName: String): String {
    return if (includePrefix) {
      "${level.name}: "
    } else {
      ""
    }
  }

  private fun Throwable?.throwableToString() = createThrowableMsg("", this)

  private tailrec fun createThrowableMsg(msg: String, throwable: Throwable?): String {
    return if (throwable == null || throwable.cause == throwable) {
      msg
    } else {
      createThrowableMsg("$msg, Caused by: '${throwable.message}'", throwable.cause)
    }
  }
}