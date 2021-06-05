import kotlin.test.Test
import kotlin.test.assertEquals

class DocumentTest {
    @Test
    fun findSimpleDuplicates() {
        val list = instantiate("will you find me alas find me in the end", 2)
        assertEquals(1, list.size)
        val first = list.first()
        assertEquals("find me", first.text)
        assertEquals(listOf(2, 5), first.indexes)
    }

    @Test
    fun kEq3() {
        val list = instantiate("you find me here alas find me here in the end", 3)
        assertEquals(1, list.size)
        assertEquals("find me here", list.first().text)
    }

    @Test
    fun kEqWordsCount() {
        val list = instantiate("you find me", 3)
        assertEquals(0, list.size)
    }

    @Test
    fun kLessThanWordCount() {
        val list = instantiate("you", 3)
        assertEquals(0, list.size)
    }

    @Test
    fun threeMatches() {
        val list = instantiate("aa bb cc x bb cc y bb cc", k = 2)
        assertEquals(listOf(1, 4, 7), list.first().indexes)
    }

    @Test
    fun multipleDuplicates() {
        val list = instantiate("aa bb cc x bb cc y aa bb", k = 2)
        assertEquals(2, list.size)
        assertEquals(setOf("aa bb", "bb cc"), list.map { it.text }.toSet())
    }


    private fun instantiate(text: String, k: Int): List<Duplicates> {
        val instance = Document(text, k = k)
        return instance.duplicates()
    }

}