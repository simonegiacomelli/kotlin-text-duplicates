class Document(val text: String, val k: Int) {
    companion object {
        val stopChar = setOf(".", " ")
    }

    fun duplicates(): List<Duplicates> {
        val tokens = tokenize(text).toList()

        val dups = (0..tokens.size - k).map {

            val tokenSlice = tokens.subList(it, it + k)
            println("$it $tokenSlice")
            tokenSlice.map { it.text }.joinToString(" ") to tokenSlice
        }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size > 1 }
            .map { Duplicates(it.key, range(it.value)) }


        return dups
    }

    private fun range(value: List<List<Token>>): List<IntRange> {
        return value.map {
            it.first().range.first..it.last().range.last
        }
    }
}


class Duplicates(val text: String, val ranges: List<IntRange>)