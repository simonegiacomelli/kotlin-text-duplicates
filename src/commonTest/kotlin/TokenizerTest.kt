import kotlin.test.Test
import kotlin.test.assertEquals

class TokenizerTest {

    @Test
    fun empty() {
        assertEquals(0, target("").size)
    }

    @Test
    fun simple() {
        val target = target("aa bb")
        assertEquals(listOf("aa", "bb"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4), target.map { it.range })
    }

    @Test
    fun threeWords() {
        val target = target("aa bb cc")
        assertEquals(listOf("aa", "bb", "cc"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4, 6..7), target.map { it.range })
    }

    @Test
    fun punctuation() {
        val target = target("aa:bb!cc")
        assertEquals(listOf("aa", "bb", "cc"), target.map { it.text })
        assertEquals(listOf(0..1, 3..4, 6..7), target.map { it.range })
    }

    @Test
    fun multiplePunctuation() {
        val target = target("aa:;bb!?cc")
        assertEquals(listOf("aa", "bb", "cc"), target.map { it.text })
        assertEquals(listOf(0..1, 4..5, 8..9), target.map { it.range })
    }

    private fun target(s: String) = tokenize(s).toList()

}