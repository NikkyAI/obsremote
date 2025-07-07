import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual val FILESYSTEM: FileSystem = FileSystem.SYSTEM
actual fun cwd(): Path = File(".").absolutePath.toPath(normalize = true)