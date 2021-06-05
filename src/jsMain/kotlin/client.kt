import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.*
import org.w3c.dom.events.Event
import rpc.ApiDuplicates
import rpc.ApiFindRequest
import rpc.ApiRequestSum

fun main() {
    println("Js v0.7")
    GlobalScope.launch {
        val response = Api.send(ApiRequestSum(5, 7))
        console.log(response)
    }
    window.onload = { document.body?.sayHello() }
    var dups: List<ApiDuplicates> = emptyList()
    var occIndex = 0

    val docu = Docu()
    val trix: HTMLElement by docu
    val dupsSelect: HTMLSelectElement by docu

    fun dup() = dups[dupsSelect.selectedIndex]

    val btnPrev: HTMLButtonElement by docu
    val btnNext: HTMLButtonElement by docu
    val spanPattern: HTMLElement by docu
    val spanOccurrences: HTMLElement by docu

    val trixdyn: dynamic = trix
    val editor = trixdyn.editor
    fun show(dup: ApiDuplicates, index: Int = 0) {
        val occ = dup.ranges[index]
        editor.setSelectedRange(arrayOf(occ.first, occ.last + 1))
        val rect = editor.getClientRectAtPosition(occ.first)
        console.log(rect)
        window.scrollTo(rect.left, rect.top)
    }

    fun changeOccurrence(offset: Int) {
        occIndex += offset
        val occSize = dup().ranges.size
        occIndex = occIndex.mod(occSize)
        spanOccurrences.innerHTML = "Showing occurrence ${occIndex + 1}/${occSize}"
        show(dup(), occIndex)
    }
    btnPrev.onclick = { changeOccurrence(-1); true }
    btnNext.onclick = { changeOccurrence(1); true }
    dupsSelect.onchange = {
        occIndex = 0
        if (dups.isNotEmpty()) {
            changeOccurrence(0)
        }
        true
    }

    suspend fun trixChange() {
        console.log("kt trix-change")

        val content = editor.getDocument().toString()

        dups = Api.send(ApiFindRequest(content, 3)).duplicates
        val message = if (dups.isEmpty()) "No duplicate patterns found" else "Found ${dups.size} duplicate patterns"
        spanPattern.innerHTML = message
        println(message)

        dupsSelect.options.also { while (it.length > 0) it.remove(0) }

        dups.forEach {
            println(it.text + " range: ${it.ranges}")
            val option = document.createElement("option") as HTMLOptionElement
            option.text = it.text + " - ${it.ranges.size} occurrences"
            dupsSelect.options.add(option)
        }

    }

    trix.addEventListener("trix-change", { event: Event -> GlobalScope.launch { trixChange() } })
}

fun Node.sayHello() {
    append {
        div {
            +"Hello from JS"
        }
    }
}