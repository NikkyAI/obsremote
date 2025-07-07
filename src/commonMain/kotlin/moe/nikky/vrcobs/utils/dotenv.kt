//package moe.nikky.vrcobs.utils
//
//import io.github.cdimascio.dotenv.dotenv
//import io.github.oshai.kotlinlogging.KotlinLogging
//
//private val logger by lazy {
//    KotlinLogging.logger {}
//}
//
//val dotenv by lazy {
//    dotenv {
//        this.ignoreIfMissing = true
////    val logger = KotlinLogging.logger {}
////    val path = File(".env").absolutePath
////    logger.info { "looking for .env at $path" }
//    }.also {
////        withLoggingContext("module" to "dotenv") {
////            val allData = it.entries() // .associate { it.key to it.value }
////            val fromFile = it.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE) // .associate { it.key to it.value }
////            logger.info { "loaded ${fromFile.size} from .env" }
////            logger.info { "loaded ${System.getenv().size} from env" }
////            logger.info { "loaded total: ${allData.size}" }
////        }
//    }
//}