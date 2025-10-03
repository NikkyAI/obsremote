import kotlinx.cinterop.*
import platform.posix.*

@OptIn(ExperimentalForeignApi::class)
actual fun exec(
    vararg command: String,
    trim: Boolean,
    redirectStderr: Boolean
): String {
    val joinedCommand = command.joinToString(" ")
    val commandToExecute = if (redirectStderr) "$joinedCommand 2>&1" else joinedCommand
    val fp = memScoped {
        popen?.invoke(commandToExecute.cstr.ptr, "r".cstr.ptr) ?: error("Failed to run command: $command")
    }

    val stdout = buildString {
        val buffer = ByteArray(4096)
        while (true) {
            val input = fgets(buffer.refTo(0), buffer.size, fp) ?: break
            append(input.toKString())
        }
    }

    val status = pclose?.invoke(fp)
    if (status != 0) {
        error("Command `[${command.joinToString()}]` failed with status $status${if (redirectStderr) ": $stdout" else ""}")
    }

    return if (trim) stdout.trim() else stdout
}
