import kotlinx.browser.document
import org.w3c.dom.Element
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class Docu {

    inline operator fun <reified T> getValue(thisRef: Any?, property: KProperty<*>): T {
        val name = property.name
        val element = document.getElementById(name)
            ?: error("Element [$name] was not found with document.getElementById($name)")
        if (!T::class.isInstance(element))
            throw ClassCastException(
                "The element instance is of type: ${element::class.js.name}" +
                        " but the type of the delegate is: ${(T::class as KClass<out Element>).js.name}"
            )
        return element as T
    }

}