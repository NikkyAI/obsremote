package dotenv

import dotenv.internal.DotenvParser
import dotenv.internal.DotenvReader
import io.ktor.util.unmodifiable
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Builds and loads and [Dotenv] instance.
 * @see Dotenv.configure
 */
class DotenvBuilder {
    private var filename: String = ".env"
    private var directoryPath: Path = "./".toPath()
    private var systemProperties = false
    private var throwIfMissing = true
    private var throwIfMalformed = true

    /**
     * Sets the directory containing the .env file.
     * @param path the directory containing the .env file
     * @return this [DotenvBuilder]
     */
    fun directory(path: Path): DotenvBuilder {
        this.directoryPath = path
        return this
    }

    /**
     * Sets the name of the .env file. The default is .env.
     * @param name the filename
     * @return this [DotenvBuilder]
     */
    fun filename(name: String): DotenvBuilder {
        filename = name
        return this
    }

    /**
     * Does not throw an exception when .env is missing.
     * @return this [DotenvBuilder]
     */
    fun ignoreIfMissing(): DotenvBuilder {
        throwIfMissing = false
        return this
    }

    /**
     * Does not throw an exception when .env is malformed.
     * @return this [DotenvBuilder]
     */
    fun ignoreIfMalformed(): DotenvBuilder {
        throwIfMalformed = false
        return this
    }

    /**
     * Sets each environment variable as system properties.
     * @return this [DotenvBuilder]
     */
    fun systemProperties(): DotenvBuilder {
        systemProperties = true
        return this
    }

    /**
     * Load the contents of .env into the virtual environment.
     * @return a new [Dotenv] instance
     * @throws DotenvException when an error occurs
     */
    @Throws(DotenvException::class)
    fun load(fileSystem: FileSystem): Dotenv {
        val reader: DotenvParser = DotenvParser(
            DotenvReader(directoryPath, filename),
            throwIfMissing, throwIfMalformed
        )
        val env: List<DotenvEntry> = reader.parse(fileSystem)
        if (systemProperties) {
            env.forEach{ it ->
                setProperty(
                    it.key,
                    it.value
                )
            }
        }

        return DotenvImpl(env)
    }

    internal class DotenvImpl(envVars: List<DotenvEntry>) : Dotenv {
        private val envVars: Map<String, String>
        private val set: Set<DotenvEntry>
        private val setInFile: Set<DotenvEntry>

        init {
            val envVarsInFile: Map<String, String> =
                envVars.associate {
                    it.key to it.value
                }
//                    .collect(toMap(DotenvEntry::getKey, DotenvEntry::getValue, { a, b -> b }))

            this.envVars = HashMap(envVarsInFile)
            this.envVars.putAll(getEnv())

            this.set =
                this.envVars
                    .map { (key, value) ->
                        DotenvEntry(key, value)
                    }
                    .toSet()
                    .unmodifiable()
//                    .entrySet()
//                    .stream()
//                    .map({ it -> DotenvEntry(it.getKey(), it.getValue()) })
//                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet))

            this.setInFile =
                envVarsInFile
                    .map { (key, value) ->
                        DotenvEntry(key, value)
                    }
                    .toSet()
                    .unmodifiable()

//                    .entrySet()
//                    .stream()
//                    .map({ it -> DotenvEntry(it.getKey(), it.getValue()) })
//                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet))
        }

        override fun entries(): Set<DotenvEntry> {
            return set
        }

        override fun entries(filter: Dotenv.Filter?): Set<DotenvEntry> {
            return if (filter == null) entries() else setInFile
        }

        override fun get(key: String): String? {
            val value: String? = getEnv(key)
            return value ?: envVars[key]
        }

        override fun get(key: String, defaultValue: String?): String? {
            val value = this.get(key)
            return value ?: defaultValue
        }
    }
}