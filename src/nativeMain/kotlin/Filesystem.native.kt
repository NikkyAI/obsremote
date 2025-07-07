import okio.FileSystem
import okio.Path
import okio.SYSTEM
import kotlinx.cinterop.*
import okio.Path.Companion.toPath
import platform.posix.PATH_MAX
import platform.posix.getcwd

actual val FILESYSTEM: FileSystem = FileSystem.SYSTEM

@OptIn(ExperimentalForeignApi::class)
actual fun cwd(): Path = memScoped {
    val temp = allocArray<ByteVar>(PATH_MAX + 1)
    getcwd(temp, PATH_MAX.convert())
    temp.toKString()
}.toPath(normalize = true)
