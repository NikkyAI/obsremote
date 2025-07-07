package dotenv.internal

import dotenv.DotenvException
import kotlinx.io.IOException
import okio.Buffer
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer

/**
 * (Internal) Reads a .env file
 */
class DotenvReader
/**
 * Creates a dotenv reader
 * @param directory the directory containing the .env file
 * @param filename the file name of the .env file e.g. .env
 */constructor (private val directory: Path, private val filename: String) {
    /**
     * (Internal) Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     * @throws IOException if an I/O error occurs
     */
    @Throws(DotenvException::class, IOException::class)
    fun read(filesystem: FileSystem): List<String> {
//        val dir = directory
//            .replace("\\\\", "/")
//            .replaceFirst("\\.env$", "")
//            .replaceFirst("/$", "")
//            .toPath()

        val location = directory.normalized() / filename
//        val lowerLocation = location.toLowerCase()
//        val path: Path = if (lowerLocation.startsWith("file:")
//            || lowerLocation.startsWith("android.resource:")
//            || lowerLocation.startsWith("jimfs:")
//        )
//            Paths.get(URI.create(location))
//        else
//            Paths.get(location)
        val path = location

//        println("reading $path")
        val metadata = filesystem.metadata(path)
//        if (filesystem.exists(path)) {
            return filesystem.read(path) {
                val bytes = ByteArray(metadata.size?.toInt() ?: (1024 * 1024))
                readFully(bytes)
                bytes.decodeToString().lines()
//
//                buildList {
//                    while(!exhausted()) {
//                        add()
//                    }
//                }
            }
//        } else {
//
//        }

//        try {
//            return dotenv.internal.ClasspathHelper.loadFileFromClasspath(location.replaceFirst("^\\./", "/"))
//                .collect(Collectors.toList())
//        } catch (e: DotenvException) {
//            val cwd: Path? = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize()
//            val cwdMessage = if (!path.isAbsolute()) "(working directory: " + cwd + ")" else ""
//            e.addSuppressed(DotenvException("Could not find " + path + " on the file system " + cwdMessage))
//            throw e
//        }
    }
}