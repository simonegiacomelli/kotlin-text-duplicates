import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
    @Test
    fun simple() {
        val target = tokenize("aa bb")
        assertEquals(listOf("aa", "bb"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4), target.map { it.range })
    }

    @Test
    fun threeWords() {
        val target = tokenize("aa bb cc")
        assertEquals(listOf("aa", "bb", "cc"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4, 6..7), target.map { it.range })
    }

}

fun tokenize(s: String): List<Token> {
    val matches = """\p{Space}""".toRegex().findAll(s).iterator()
    val sequence = sequence {
        if (!matches.hasNext()) return@sequence

        val m0 = matches.next()
        yield(0 until m0.range.first)
        var last = m0.range.last
        while (matches.hasNext()) {
            val m = matches.next()
            yield(last + 1 until m.range.first)
            last = m.range.last
        }
        yield(last + 1 until s.length)
    }

    val sequence1 = sequence.toList()
    return sequence1.map { Token(s.substring(it), it) }.toList()
}

class Token(val text: String, val range: IntRange = (0..0))