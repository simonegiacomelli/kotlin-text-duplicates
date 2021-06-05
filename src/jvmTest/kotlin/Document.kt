class Document(val text: String, val k: Int) {
    fun duplicates(): List<Duplicates> {
        val words = text.split(" ")

        val dups = (0..words.size - k).map {

            val subList = words.subList(it, it + k)
            println("$it $subList")
            subList.joinToString(" ") to it
        }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size > 1 }
            .map { Duplicates(it.key, it.value.sorted()) }
        return dups
    }
}

class Duplicates(val text: String, val indexes: List<Int>)