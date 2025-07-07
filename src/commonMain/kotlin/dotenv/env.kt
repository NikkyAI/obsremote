package dotenv

expect fun getEnv(key: String): String?
expect fun getEnv(): Map<String, String>
expect fun setProperty(key: String, value: String)
