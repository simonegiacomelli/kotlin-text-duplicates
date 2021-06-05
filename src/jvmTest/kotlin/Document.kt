class Document(val text: String, val k: Int) {
    companion object {
        val stopChar = setOf(".", " ")
    }

    fun duplicates(): List<Duplicates> {
        //list<T>.windowed(...)
        val words = text.split(*stopChar.toTypedArray()).filterNot { it.isBlank() }
//        val words = text.split("\\p{Punct}|\\p{Space}".toRegex() )

        val dups = (0..words.size - k).map {

            val subList = words.subList(it, it + k)
            println("$it $subList")
            subList.joinToString(" ") to it
        }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size > 1 }
            .map { Duplicates(it.key, it.value.sorted().map { it..it }) }
        return dups
    }
}

typealias Range = ClosedRange<Int>

class Duplicates(val text: String, val ranges: List<Range>)