class Document(private val text: String, val k: Int) {

    fun duplicates(): List<Duplicates> {
        val tokens = tokenize(removeDiacritics(text)).toList()
        return (0..tokens.size - k).map { tokenIndex ->

            val tokenSlice = tokens.subList(tokenIndex, tokenIndex + k)
            tokenSlice.joinToString(" ") { it.text } to tokenSlice
        }
            .groupBy(keySelector = { it.first }, valueTransform = { it.second })
            .filter { it.value.size > 1 }
            .map { Duplicates(it.key, range(it.value)) }
    }

    private fun range(value: List<List<Token>>) = value.map { it.first().range.first..it.last().range.last }
}


class Duplicates(val text: String, val ranges: List<IntRange>)