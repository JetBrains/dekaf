package org.jetbrains.dekaf.util

import org.jetbrains.dekaf.core.Settings
import java.util.*


fun settingsOf(vararg strings: String) = Settings(*strings)


fun Settings.override(vararg items: Pair<String,String>): Settings =
    this.override(mapOf(*items))


fun Settings.override(items: Map<String,String>): Settings {
    if (items.isEmpty()) return this
    if (this.isEmpty) return Settings(items)

    val map = TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER)
    map.putAll(this.toMap())
    for (item in items) {
        val key = item.key.trim()
        val value = item.value.trim()

        if (value.isNotEmpty()) map[key] = value
        else map.remove(key)
    }

    return Settings(map)
}
