/**
 * dotenv-java
 * copyright 2021 - Carmine DiMascio
 * license - Apache 2.0
 */
package dotenv

import okio.FileSystem

/**
 * Creates and configures a new Dotenv instance
 */
interface Dotenv {
    /**
     * A dotenv entry filter
     */
    enum class Filter {
        /**
         * Filter matching only environment variables declared in the .env file
         */
        DECLARED_IN_ENV_FILE
    }

    /**
     * Returns the set of environment variables with values
     * @return the set of [DotenvEntry]s for all environment variables
     */
    fun entries(): Set<DotenvEntry?>?

    /**
     * Returns the set of [DotenvEntry]s matching the filter
     * @param filter the filter e.g. [Dotenv.Filter]
     * @return the set of [DotenvEntry]s for environment variables matching the [Dotenv.Filter]
     */
    fun entries(filter: Filter?): Set<DotenvEntry>?

    /**
     * Retrieves the value of the environment variable specified by key
     * @param key the environment variable
     * @return the value of the environment variable
     */
    operator fun get(key: String): String?

    /**
     * Retrieves the value of the environment variable specified by key.
     * If the key does not exist, then the default value is returned
     * @param key the environment variable
     * @param defaultValue the default value to return
     * @return the value of the environment variable or default value
     */
    fun get(key: String, defaultValue: String? = null): String?

    companion object {
        /**
         * Configures a new [Dotenv] instance
         * @return a new [Dotenv] instance
         */
        fun configure(): DotenvBuilder {
            return DotenvBuilder()
        }

        /**
         * Creates and loads a [Dotenv] instance with default options
         * @return a new [Dotenv] instance
         */
        fun load(fileSystem: FileSystem): Dotenv {
            return DotenvBuilder().load(fileSystem)
        }
    }
}