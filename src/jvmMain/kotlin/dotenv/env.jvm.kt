package dotenv

actual fun getEnv(key: String): String? {
    return System.getenv(key)
}

actual fun setProperty(key: String, value: String) {
    System.setProperty(key, value)
}

actual fun getEnv(): Map<String, String> {
    return System.getenv()
}