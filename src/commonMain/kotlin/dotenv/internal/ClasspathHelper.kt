//package dotenv.internal
//
//import io.github.cdimascio.dotenv.DotenvException
//import java.util.Scanner
//import java.util.stream.Stream
//
///**
// * Classpath helper
// */
//object ClasspathHelper {
//    fun loadFileFromClasspath(location: String?): Stream<String?> {
//        val loader: java.lang.Class = ClasspathHelper::class.java
//        var inputStream: Unit /* TODO: class org.jetbrains.kotlin.nj2k.types.JKJavaNullPrimitiveType */? =
//            loader.getResourceAsStream(location)
//        if (inputStream == null) {
//            inputStream = loader.getResourceAsStream(location)
//        }
//        if (inputStream == null) {
//            inputStream = ClassLoader.getSystemResourceAsStream(location)
//        }
//
//        if (inputStream == null) {
//            throw DotenvException("Could not find " + location + " on the classpath")
//        }
//
//        val scanner: Scanner = Scanner(inputStream, "utf-8")
//        val lines = ArrayList<String?>()
//        while (scanner.hasNext()) {
//            lines.add(scanner.nextLine())
//        }
//        scanner.close()
//        return lines.stream()
//    }
//}