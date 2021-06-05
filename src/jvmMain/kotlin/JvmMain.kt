import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*

const val static = "static"


fun main() {
    embeddedServer(CIO, port = 8080, module = Application::module)
        .apply { start(wait = true) }
}


fun Application.module() {
    routing {
        get("/") {
            val marker = "<!-- base -->"
            val resource = Application::class.java.getResource("/js/index.html").readText()
            check(resource.contains(marker))
            val content = resource
                .replace(marker, """<base href="static/js/">""")
            call.respondText(content, contentType = ContentType.Text.Html)
        }
        static("/$static") {
            resources()
        }
    }
}