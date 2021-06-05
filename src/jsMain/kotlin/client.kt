import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.dom.append
import org.w3c.dom.Node
import org.w3c.dom.events.Event
import rpc.ApiFindRequest
import rpc.ApiRequestSum

fun main() {
    println("Js v0.6")
    GlobalScope.launch {
        val response = Api.send(ApiRequestSum(5, 7))
        console.log(response)
    }
    window.onload = { document.body?.sayHello() }
    val trix = document.getElementById("trix") ?: error("element trix not found")

    suspend fun trixChange() {
        console.log("kt trix-change")

        val trixdyn: dynamic = trix
        val editor = trixdyn.editor
        val content = editor.getDocument().toString()
        //console.log(content)

        val dups = Api.send(ApiFindRequest(content, 3)).duplicates
        println(if (dups.isEmpty()) "No duplicate patterns found" else "Found ${dups.size} duplicate patterns")
        dups.forEach {
            println(it.text + " range: ${it.ranges}")
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