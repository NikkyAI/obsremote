actual fun exec(vararg command: String, trim: Boolean, redirectStderr: Boolean): String {
    val p = ProcessBuilder(
        *command
    )
        .redirectErrorStream(redirectStderr)
        .start()

    return p.inputStream.readAllBytes().decodeToString().let {
        if(trim) it.trim() else it
    }
}