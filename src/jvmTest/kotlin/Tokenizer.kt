import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {
    @Test
    fun simple() {
        val target = tokenize("aa bb")
        assertEquals(listOf("aa", "bb"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4), target.map { it.range })
    }

}

fun tokenize(s: String): List<Token> {
    val matches = """\p{Space}""".toRegex().findAll(s).toList()
    val r0 = 0 until matches[0].range.first
    val r1 = matches[0].range.last + 1 until s.length
    return listOf(
        Token(s.substring(r0), r0), Token(s.substring(r1), r1)
    )
}

class Token(val text: String, val range: IntRange = (0..0))