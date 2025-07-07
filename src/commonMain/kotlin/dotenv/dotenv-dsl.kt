/**
 * Copyright (c) Carmine DiMascio 2017 - 2021
 * License: MIT
 */
package dotenv

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * Configure dotenv
 */
fun dotenv(fileSystem: FileSystem, block: Configuration.() -> Unit = {}): Dotenv {
    val config = Configuration()
    block(config)
    val dotenv = Dotenv.configure()
    dotenv.directory(config.directory)
    dotenv.filename(config.filename)
    if (config.ignoreIfMalformed) dotenv.ignoreIfMalformed()
    if (config.ignoreIfMissing) dotenv.ignoreIfMissing()
    if (config.systemProperties) dotenv.systemProperties()
    return dotenv.load(fileSystem)
}

/**
 * The dotenv configuration
 */
class Configuration {
    /**
     * Set the directory containing the .env file
     */
    var directory: Path = "./".toPath()
    /**
     * Sets the name of the .env. The default is .env
     */
    var filename: String = ".env"
    /**
     * Do not throw an exception when .env is malformed
     */
    var ignoreIfMalformed = false
    /**
     * Do not throw an exception when .env is missing
     */
    var ignoreIfMissing = false

    /**
     * Set env vars into System properties. Enables fetch them via e.g. System.getProperty(...)
     */
    var systemProperties = false
}