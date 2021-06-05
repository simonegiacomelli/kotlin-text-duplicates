import io.ktor.application.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import rpc.*

const val static = "static"

val contextHandler = ContextHandlers<PipelineContext<Unit, ApplicationCall>>()
    .apply {
        register { req: ApiRequestSum, context ->
            ApiResponseSum(req.a + req.b)
        }
        register { req: ApiFindRequest ->
            val dups = Document(req.text, k = req.k)
            ApiFindResponse(dups.duplicates().map { it.toApiDuplicates() })
        }

    }

fun main() {


    embeddedServer(CIO, port = 8080, module = Application::module)
        .apply { start(wait = true) }
}


fun Application.module() {
    routing {
        registerApiServerRoute()
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


fun Routing.registerApiServerRoute() {

    val path = ApiConf.baseUrl("{api_name}")
    post("$path") {
        try {
            val apiName = call.parameters["api_name"]!!
            println("dispatching $apiName")
            val serializedResponse =
                contextHandler.dispatch(apiName, call.receiveText(), this)
            call.respondText("success=1\n\n$serializedResponse", ContentType.Text.Plain)
        } catch (ex: Exception) {
            val text = "success=0\n\n${ex.stackTraceToString()}"
            println("handling exception [[$text]] ")
            call.respondText(
                text = text,
                status = HttpStatusCode.InternalServerError,
                contentType = ContentType.Text.Plain
            )
        }
    }
}