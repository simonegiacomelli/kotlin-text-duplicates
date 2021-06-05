import java.lang.Integer.max
import java.lang.Integer.min
import kotlin.test.*

class DocumentTest {
    @Test
    fun findSimpleDuplicates() {
        val instance = Document("will you find me alas find me in the end", k = 2)
        val list = instance.duplicates()
        assertEquals(1, list.size)
        assertEquals("find me", list.first().text)
    }

}

class Document(val text: String, val k: Int) {
    fun duplicates(): List<Duplicates> {
        val words = text.split(" ")

        val dups = words.drop(1).indices.map {

            val subList = words.subList(it, it + k)
            println(it.toString() + " " + subList)
            subList.joinToString(" ") to it }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size > 1 }
            .map { Duplicates(it.key) }
        return dups
//        { index ->
//            words.subList(index, k).joinToString(" ") to index
//        }
    }
}

class Duplicates(val text: String) {

}