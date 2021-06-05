import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.Node
import org.w3c.dom.events.Event

fun main() {
    println("Js v0.5")
    window.onload = { document.body?.sayHello() }
    val trix = document.getElementById("trix") ?: error("element trix not found")
    trix.addEventListener("trix-change", { event: Event ->
        console.log("kt trix-change")
        val trixdyn: dynamic = trix
        val editor = trixdyn.editor
        val content = editor.getDocument().toString()
        //console.log(content)
        val dups = Document(content, k = 3).duplicates()
        println(if (dups.isEmpty()) "No duplicate patterns found" else "Found ${dups.size} duplicate patterns")
        dups.forEach {
            println(it.text + " range: ${it.ranges}")
        }
    })
}

fun Node.sayHello() {
    append {
        div {
            +"Hello from JS"
        }
    }
}