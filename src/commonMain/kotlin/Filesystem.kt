import okio.FileSystem
import okio.Path

expect val FILESYSTEM: FileSystem

expect fun cwd(): Path
