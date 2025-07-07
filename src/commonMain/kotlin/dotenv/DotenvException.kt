package dotenv

/**
 * Signals that dotenv exception of some sort has occurred.
 */
class DotenvException : RuntimeException {
    /**
     * Create a dotenv runtime exception with the specified detail message
     * @param message the detail message
     */
    constructor(message: String?) : super(message)

    /**
     * Creates a dotenv runtime exception
     * @param cause the cause
     */
    constructor(cause: Throwable?) : super(cause)
}