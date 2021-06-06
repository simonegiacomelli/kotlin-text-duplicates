import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLElement

fun HTMLElement.app_flash() {
    classList.add("flash")
    window.setTimeout({
        classList.remove("flash")
    }, 5000)
}


fun scrollSelectionIntoView() {
    // Get current selection
    val w: dynamic = window
    val selection = w.getSelection();

    // Check if there are selection ranges
    if (!selection.rangeCount) {
        return;
    }

    // Get the first selection range. There's almost never can be more (instead of firefox)
    val firstRange = selection.getRangeAt(0);

    // Create empty span that will be used as an anchor for scroll, because it's imposible to do it with just text nodes
    val tempAnchorEl = document.createElement("span");

    // Put the span right after caret position
    firstRange.insertNode(tempAnchorEl)

    // Scroll to the span. I personally prefer to add the block start option, but if you want to use 'end' instead just replace span to br
    tempAnchorEl.scrollIntoView(js("""{behavior: "smooth", block: "center", inline: "nearest"}"""))

    // Remove the anchor because it's not needed anymore
    tempAnchorEl.remove();
}