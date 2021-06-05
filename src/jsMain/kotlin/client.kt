import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLOptionElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.events.Event
import rpc.ApiDuplicates
import rpc.ApiFindRequest
import rpc.ApiRequestSum

fun main() {
    println("Js v0.8")
    GlobalScope.launch {
        val response = Api.send(ApiRequestSum(5, 7))
        console.log(response)
    }
    window.onload = { }
    var dups: List<ApiDuplicates> = emptyList()
    var occIndex = 0
    var dupsCount = 0

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
        if (dups.isEmpty()) return
        val occSize = dup().ranges.size
        occIndex += offset
        occIndex = occIndex.mod(occSize)
        spanOccurrences.innerHTML = "Showing occurrence ${occIndex + 1}/${occSize}"
        show(dup(), occIndex)
    }

    btnPrev.onclick = { changeOccurrence(-1); true }
    btnNext.onclick = { changeOccurrence(1); true }
    fun dupsSelectChange() {
        println("dupsSelectChange")
        occIndex = 0
        if (dups.isNotEmpty()) changeOccurrence(0)
    }
    dupsSelect.onchange = { dupsSelectChange(); true }

    suspend fun trixChange() {
        console.log("kt trix-change")

        val content = editor.getDocument().toString()

        dups = Api.send(ApiFindRequest(content, 3)).duplicates
        val message = if (dups.isEmpty()) "No duplicate patterns found" else "Found ${dups.size} duplicate patterns."
        spanPattern.innerHTML = message
        println(message)

        dupsSelect.options.also { while (it.length > 0) it.remove(0) }

        dups.forEach {
            println(it.text + " range: ${it.ranges}")
            val option = document.createElement("option") as HTMLOptionElement
            option.text = it.text + " - ${it.ranges.size} occurrences"
            dupsSelect.options.add(option)
        }
        occIndex = -1
        spanOccurrences.innerHTML = ""

        if (dups.size != dupsCount) {
            dupsCount = dups.size
            spanPattern.app_flash()
            if (dups.size > 1)
                dupsSelect.app_flash()
        }

    }

    fun trixChangeSusp() = GlobalScope.launch { trixChange() }
    trix.addEventListener("trix-change", { event: Event -> trixChangeSusp() })
    trixChangeSusp()
}

