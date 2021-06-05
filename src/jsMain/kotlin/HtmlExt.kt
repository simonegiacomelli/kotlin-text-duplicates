import kotlinx.browser.window
import org.w3c.dom.HTMLElement

fun HTMLElement.app_flash() {
    classList.add("flash")
    window.setTimeout({
        classList.remove("flash")
    }, 5000)
}
