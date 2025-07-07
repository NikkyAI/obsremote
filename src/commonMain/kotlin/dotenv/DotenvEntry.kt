package dotenv

/**
 * A key value pair representing an environment variable and its value
 */
data class DotenvEntry
/**
 * Creates a new dotenv entry using the provided key and value
 * @param key the dotenv entry name
 * @param value the dotenv entry value
 */(
    /**
     * Returns the key for the [DotenvEntry]
     * @return the key for the [DotenvEntry]
     */
    val key: String,
    /**
     * Returns the value for the [DotenvEntry]
     * @return the value for the [DotenvEntry]
     */
    val value: String
) {
    override fun toString() = "$key=$value"
}
