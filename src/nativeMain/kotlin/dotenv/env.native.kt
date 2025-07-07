package dotenv

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.get
import kotlinx.cinterop.toKString
import platform.posix.environ
import platform.posix.getenv
import platform.posix.putenv

@OptIn(ExperimentalForeignApi::class)
actual fun getEnv(key: String): String? {
    environ
    return getenv(key)?.toKString()
}

actual fun setProperty(key: String, value: String) {
    putenv("$key=$value")
}

@OptIn(ExperimentalForeignApi::class)
actual fun getEnv(): Map<String, String> {
    return buildMap {
        for (i in 0..Int.MAX_VALUE) {
            val kv = environ?.get(i)?.toKString() ?: break
            this[kv.substringBefore('=')] = kv.substringAfter('=', "")
        }
    }
}