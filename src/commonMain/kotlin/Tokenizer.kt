fun tokenize(s: String): Sequence<Token> {
    val matches = """\p{Space}|\p{Punct}""".toRegex().findAll(s).iterator()
    val sequence = sequence {

        suspend fun SequenceScope<IntRange>.y(r: IntRange) = if (r.isEmpty()) Unit else yield(r)

        var last = -1
        while (matches.hasNext()) {
            val m = matches.next()
            y(last + 1 until m.range.first)
            last = m.range.last
        }
        y(last + 1 until s.length)
    }

    return sequence.map { Token(s.substring(it), it) }
}

class Token(val text: String, val range: IntRange)