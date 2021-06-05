fun removeDiacritics(str: String): String {
    val res: List<Char> = str.map {
        diacriticsMap[it] ?: it
    }
    return res.toCharArray().concatToString()
}

val diacriticsMap = createDiacriticsMapFix()

private fun createDiacriticsMapFix(): Map<Char, Char> {
    //the original version has problems with the IR compiler in produzione
    return diacriticsMapComputed
        .split("\n")
        .asSequence()
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map {
            val parts = it.split("=", limit = 2)
            val key = parts[0]
            val value = parts[1]
            if (value.length > 1) error("len? [$value]")
            key.toInt() to value.first()
        }.associate {
            it.first.toChar() to it.second
        }
}
