package dotenv.internal

import dotenv.DotenvEntry
import dotenv.DotenvException
import okio.FileSystem
import okio.IOException

/**
 * (Internal) Parses .env file
 */
class DotenvParser(reader: dotenv.internal.DotenvReader, throwIfMissing: Boolean, throwIfMalformed: Boolean) {
    private val reader: dotenv.internal.DotenvReader
    private val throwIfMissing: Boolean
    private val throwIfMalformed: Boolean

    private val parseLine: (String) -> DotenvEntry? = { s ->
        dotenv.internal.DotenvParser.Companion.matchEntry(
            dotenv.internal.DotenvParser.Companion.DOTENV_ENTRY_REGEX,
            s
        )
    }

    /**
     * Creates a dotenv parser
     *
     * @param reader           the dotenv reader
     * @param throwIfMissing   if true, throws when the .env file is missing
     * @param throwIfMalformed if true, throws when the .env file is malformed
     */
    init {
        this.reader = reader
        this.throwIfMissing = throwIfMissing
        this.throwIfMalformed = throwIfMalformed
    }

    /**
     * (Internal) parse the .env file
     *
     * @return a list of DotenvEntries
     * @throws DotenvException if an error is encountered during the parse
     */
    @Throws(DotenvException::class)
    fun parse(fileSystem: FileSystem): List<DotenvEntry> {
        val lines = lines(fileSystem)
        val entries: ArrayList<DotenvEntry> = ArrayList<DotenvEntry>()

        var currentEntry: String? = ""
        for (line in lines) {
            if (currentEntry.equals("") && (isWhiteSpace(line) || isComment(line) || isBlank(line))
            ) continue

            currentEntry += line

            val entry = parseLine(currentEntry)
            if (entry == null) {
                if (throwIfMalformed) throw DotenvException("Malformed entry " + currentEntry)
                currentEntry = ""
                continue
            }

            var value = entry.value
            if (QuotedStringValidator.startsWithQuote(value) && !QuotedStringValidator.endsWithQuote(
                    value
                )
            ) {
                currentEntry += "\n"
                continue
            }
            if (!QuotedStringValidator.isValid(entry.value)) {
                if (throwIfMalformed) throw DotenvException("Malformed entry, unmatched quotes " + line)
                currentEntry = ""
                continue
            }
            val key = entry.key
            value = QuotedStringValidator.stripQuotes(entry.value)
            entries.add(DotenvEntry(key, value))
            currentEntry = ""
        }

        return entries
    }

    @Throws(DotenvException::class)
    private fun lines(fileSystem: FileSystem): List<String> {
        try {
            return reader.read(fileSystem)
        } catch (e: DotenvException) {
            if (throwIfMissing) throw e
            return emptyList()
        } catch (e: IOException) {
            throw DotenvException(e)
        }
    }

    /**
     * Internal: Validates quoted strings
     */
    private object QuotedStringValidator {
        fun isValid(input: String): Boolean {
            val s = input.trim()
            if (isNotQuoted(s)) {
                return true
            }
            if (doesNotStartAndEndWithQuote(s)) {
                return false
            }

            return !hasUnescapedQuote(s) // No unescaped quotes found
        }

        fun hasUnescapedQuote(s: String): Boolean {
            var hasUnescapedQuote = false
            // remove start end quote
            val content = s.substring(1, s.length - 1)
            val quotePattern = "\"".toRegex()

            hasUnescapedQuote = quotePattern.findAll(content).any { matchResult ->
                val quoteIndex = matchResult.range.start
                quoteIndex == 0 || content[(quoteIndex - 1)] != '\\' // unescaped quote found
            }
//
//            // Check for unescaped quotes
//            while (matcher.find()) {
//                val quoteIndex: Int = matcher.start()
//                // Check if the quote is escaped
//                if (quoteIndex == 0 || content.charAt(quoteIndex - 1) !== '\\') {
//                    hasUnescapedQuote = true // unescaped quote found
//                }
//            }
            return hasUnescapedQuote
        }

        fun doesNotStartAndEndWithQuote(s: String): Boolean {
            return s.length == 1 || !(startsWithQuote(s) && endsWithQuote(
                s
            ))
        }

        fun endsWithQuote(s: String): Boolean {
            return s.endsWith("\"")
        }

        fun startsWithQuote(s: String): Boolean {
            return s.startsWith("\"")
        }

        fun isNotQuoted(s: String): Boolean {
            return !startsWithQuote(s) && !endsWithQuote(
                s
            )
        }

        fun stripQuotes(input: String): String {
            val tr = input.trim()
            return if (isQuoted(tr)) tr.substring(
                1,
                input.length - 1
            ) else tr
        }
    }

    companion object {
        private val WHITE_SPACE_REGEX: Regex = "^\\s*$".toRegex() // ^\s*${'$'}

        // The follow regex matches key values.
        // It supports quoted values surrounded by single or double quotes
        // -  Single quotes: ['][^']*[']
        //    The above regex snippet matches a value wrapped in single quotes.
        //    The regex snippet does not match internal single quotes. This is present to allow the trailing comment to include single quotes
        // -  Double quotes: same logic as single quotes
        // It ignore trailing comments
        // - Trailing comment: \s*(#.*)?$
        //   The above snippet ignore spaces, the captures the # and the trailing comment
        private val DOTENV_ENTRY_REGEX: Regex =
            "^\\s*([\\w.\\-]+)\\s*(=)\\s*(['][^']*[']|[\"][^\"]*[\"]|[^#]*)?\\s*(#.*)?$".toRegex() //"^\\s*([\\w.\\-]+)\\s*(=)\\s*([^#]*)?\\s*(#.*)?$"); // ^\s*([\w.\-]+)\s*(=)\s*([^#]*)?\s*(#.*)?$

        private val isWhiteSpace: (String) -> Boolean = { s ->
            dotenv.internal.DotenvParser.Companion.matches(
                dotenv.internal.DotenvParser.Companion.WHITE_SPACE_REGEX,
                s
            )
        }
        private val isComment: (String) -> Boolean = { s -> s.startsWith("#") || s.startsWith("////") }
        private val isQuoted: (String) -> Boolean = { s -> s.length > 1 && s.startsWith("\"") && s.endsWith("\"") }

        private fun matches(regex: Regex, text: String): Boolean {
            return regex.matches(text)
        }

        private fun matchEntry(regex: Regex, text: String): DotenvEntry? {
            val matcher = regex.matchEntire(text)
            if (matcher == null || matcher.groups.size < 3) return null

            //TODO: possibly adjust indices to 0 and 2
            return DotenvEntry(matcher.groups[1]!!.value, matcher.groups[3]!!.value)
        }

        private fun isBlank(s: String?): Boolean {
            return s == null || s.trim().isEmpty()
        }
    }
}
