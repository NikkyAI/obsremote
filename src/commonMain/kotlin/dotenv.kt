import dotenv.dotenv
import dotenv.getEnv
import okio.Path.Companion.toPath

val cwd by lazy {
    cwd()
}
val localDotenvFile = cwd / ".env"
val dotenvFolder by lazy {
    if (FILESYSTEM.exists(localDotenvFile)) {
        cwd
    } else {
        val homedir = getEnv("HOME")?.toPath()
            ?: getEnv("HOMEPATH")?.toPath()
            ?: error("cannot find home directory from envionment")
        homedir / ".config/obsremote/"
    }
}

val DOTENV = dotenv(FILESYSTEM) {
//    val cwd = cwd()
//    val localDotenvFile = ".env".toPath(normalize = true)
    if(!FILESYSTEM.exists(dotenvFolder)) {
        FILESYSTEM.createDirectory(dotenvFolder, mustCreate = true)
    }
    val dotenvFile = dotenvFolder / ".env"
    if(!FILESYSTEM.exists(dotenvFile)) {
//        FILESYSTEM.createDirectory(dotenvFolder)
        FILESYSTEM.write(dotenvFile, mustCreate = true) {
            this.writeUtf8("# dotenv")
        }
    }

    directory = dotenvFolder
    filename = ".env"
//    if (FILESYSTEM.exists(localDotenvFile)) {
////        println("loading ${File(".env").canonicalFile}")
//        directory = "./".toPath()
//        filename = ".env"
//    } else {
//        val homedir = getEnv("HOME")?.toPath()
//            ?: getEnv("HOMEPATH")?.toPath()
//            ?: error("cannot find home directory from envionment")
//        val dotenvFile = homedir / ".config/obsremote/.env".toPath(normalize = true)
//        directory = homedir / ".config/obsremote/"
//        filename = ".env"
//        if (!FILESYSTEM.exists(dotenvFile)) {
//            FILESYSTEM.createDirectory(dotenvFile.parent!!)
//            FILESYSTEM.write(dotenvFile, mustCreate = true) {
//                this.writeUtf8("# dotenv")
//            }
//        }
//    }
//    File(".env").takeIf { !it.exists() }?.createNewFile()
    ignoreIfMissing = true
}
