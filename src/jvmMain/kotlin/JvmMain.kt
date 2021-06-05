import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import kotlinx.html.*

const val static = "static"
fun HTML.index() {
    val jsFolder = "$static/js"
    head {
        title("Hello from Ktor!")
        script(src = "$jsFolder/trix.js") {}
        link(href = "$jsFolder/trix.css", rel = "stylesheet", type = "text/css")
    }
    body {
        div {
            +"Hello from Ktor"
        }
        div {
            id = "root"
        }
        script(src = "$jsFolder/js.js") {}
    }
}

fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module)
        .apply { start(wait = true) }
}


fun Application.module() {
    routing {
        get("/") {
            //call.respondHtml(HttpStatusCode.OK, HTML::index)
            val content = Application::class.java.getResource("/js/index.html").readText()
            call.respondText(
                content.replace(
                    "<!-- base -->", """<base href="static/js/">"""
                ), contentType = ContentType.Text.Html
            )
        }
        static("/$static") {
            resources()
        }
    }
}