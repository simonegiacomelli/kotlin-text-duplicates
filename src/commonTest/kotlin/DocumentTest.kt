import kotlin.test.Test
import kotlin.test.assertEquals

class DocumentTest {
    @Test
    fun findSimpleDuplicates() {
        val dups = instantiate("find me find me", 2)
        assertEquals(1, dups.size)
        assertEquals("find me", dups.first().text)
    }

    @Test
    fun kEq3() {
        val dups = instantiate("you find me here alas find me here in the end", 3)
        assertEquals(1, dups.size)
        assertEquals("find me here", dups.first().text)
    }

    @Test
    fun kEqWordsCount() {
        val dups = instantiate("you find me", 3)
        assertEquals(0, dups.size)
    }

    @Test
    fun kLessThanWordCount() {
        val dups = instantiate("you", 3)
        assertEquals(0, dups.size)
    }

    @Test
    fun multipleDuplicates() {
        val dups = instantiate("aa bb cc x bb cc y aa bb", 2)
        assertEquals(2, dups.size)
        assertEquals(setOf("aa bb", "bb cc"), dups.map { it.text }.toSet())
    }

    @Test
    fun handlePunctuationAndSpaces() {
        val dups = instantiate(" aa. bb cc aa bb  cc", 3)
        assertEquals(1, dups.size)
    }

    @Test
    fun handleOriginalPosition() {
        val text = " aa.   bb aa bb"
        val dups = instantiate(text, 2)
        assertEquals(1, dups.size)

        val firstStart = text.indexOf("aa")
        val firstEnd = text.indexOf("bb") + 1
        val secondStart = text.indexOf("aa", firstStart + 1)
        val secondEnd = text.indexOf("bb", secondStart + 1) + 1
        assertEquals(listOf(firstStart..firstEnd, secondStart..secondEnd), dups.first().ranges)
    }

    @Test
    fun diacritics() {
        val dups = instantiate("caffe caffè", k = 1)
        assertEquals(1, dups.size)
        assertEquals("caffe", dups.first().text)
    }

    private fun instantiate(text: String, k: Int): List<Pattern> {
        return Document(text, k = k).duplicates()
    }

}