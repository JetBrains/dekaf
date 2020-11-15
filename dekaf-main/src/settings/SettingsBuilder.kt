package org.jetbrains.dekaf.main.settings

import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.inter.settings.Settings
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 *
 */
class SettingsBuilder : Iterable<Setting>, Serializable {


    private companion object {

        const val notFound = -1 // negative
        const val mapThreshold = 7

    }


    private val entries = ArrayList<Setting>()

    @Transient
    private var map: MutableMap<String, Int>? = null


    /**
     * Access the setting by the index.
     * @param index zero-based index.
     */
    operator fun get(index: Int): Setting {
        return entries[index]
    }


    /**
     * Access the setting by the name.
     * @param name name of the setting.
     * @return the found setting or null when no such setting.
     */
    operator fun get(name: String): Serializable? {
        val index = indexOf(name)
        return if (index >= 0) entries[index].value else null
    }

    /**
     * Adds new setting or replaces the existent one.
     * @param name of the setting to add/replace.
     * @param value the (new) value.
     */
    operator fun set(name: String, value: Serializable?) {
        val index = indexOf(name)
        if (index < 0) {
            // no such value
            if (value != null) put(name, value)
        }
        else {
            // such value already exists
            if (value != null) replace(index, value)
            else remove(index)
        }
    }


    operator fun get(path: Array<String>): Serializable? {
        val n = path.size
        if (n == 0) return null

        val name1 = path[0]
        val value1 = get(name1) ?: return null
        if (n == 1) return value1

        var obj: Serializable? = value1
        for (i in 1 until n) {
            val name = path[i]
            when (obj) {
                is SettingsBuilder -> obj = obj[name]
                is Settings -> obj = obj.getEntry(name)
                else -> return null
            }
            if (obj == null) return null
        }

        return if (obj is Setting) obj.value else obj
    }

    operator fun set(path: Array<String>, value: Serializable?) {
        val n = path.size
        require(n > 0) { "The path must not be empty" }
        val name1 = path[0]
        if (n == 1) {
            set(name1, value)
            return
        }

        val obj = get(name1)
        when (obj) {
            null               -> {
                val inner = SettingsBuilder()
                set(name1, inner)
                inner[path.sliceArray(1 until n)] = value
            }
            is SettingsBuilder -> {
                obj[path.sliceArray(1 until n)] = value
            }
            else               -> {
                val pathStr = path.joinToString(separator = ".")
                throw IllegalStateException("""Failed to set value by path "$pathStr" because of path name collision.""")
            }
        }
    }


    /**
     * Adds a new setting.
     * If such setting exists — throws [SuchSettingAlreadyExistsException].
     */
    @Throws(SuchSettingAlreadyExistsException::class) @Suppress("nothing_to_inline")
    inline fun put(pair: Pair<String, Serializable>) {
        put(pair.first, pair.second)
    }

    /**
     * Adds a new setting.
     * If such setting exists — throws [SuchSettingAlreadyExistsException].
     */
    @Throws(SuchSettingAlreadyExistsException::class)
    fun put(name: String, value: Serializable) {
        val existent = indexOf(name)
        if (existent < 0) addNewEntry(name, value)
        else throw SuchSettingAlreadyExistsException("The setting named '$name' already exists at index $existent")
    }


    @Throws(NoSuchSettingException::class)
    fun replace(name: String, newValue: Serializable) {
        val index = indexOf(name)
        if (index < 0) throw NoSuchSettingException("Attempting to replace a setting name '$name' but no setting")
        val newEntry = Setting(name, newValue)
        entries[index] = newEntry
    }

    fun replace(index: Int, newValue: Serializable) {
        val oldEntry = entries[index]
        val newEntry = Setting(oldEntry.name, newValue)
        entries[index] = newEntry
    }

    fun remove(name: String) {
        val index = indexOf(name)
        if (index >= 0) {
            map?.remove(name)
            entries.removeAt(index)
        }
    }

    fun remove(index: Int) {
        val entry = entries[index]
        map?.remove(entry.name)
        entries.removeAt(index)
    }


    private fun addNewEntry(name: String, value: Serializable) {
        val entry = Setting(name, value)
        val n = entries.size
        entries.add(entry)

        var map = this.map
        if (map != null) {
            map[name] = n
        }
        else if (n >= mapThreshold) {
            map = HashMap()
            for (i in 0..n) map[entries[i].name] = i
            this.map = map
        }
    }


    fun indexOf(name: String): Int {
        val map = this.map
        if (map != null) {
            val index = map[name]
            return if (index != null && index >= 0) index else notFound
        }
        else {
            for (i in 0 until entries.size)
                if (entries[i].name == name) return i
            return notFound
        }
    }


    val size: Int
        get() = entries.size

    val isNotEmpty: Boolean
        get() = entries.isNotEmpty()

    val isEmpty: Boolean
        get() = entries.isEmpty()


    override fun iterator(): Iterator<Setting> {
        return Collections.unmodifiableCollection(entries).iterator()
    }


    fun build(): Settings {
        val n = entries.size
        if (n == 0) return Settings.empty

        val list = ArrayList<Setting>(n)
        for (i in 0 until n) {
            val setting = entries[i]
            when (val value = setting.value) {
                is Settings -> if (value.isNotEmpty) list += setting
                is SettingsBuilder -> if (value.isNotEmpty) value.buildNestedSettingTo(list, setting.name)
                else -> list += setting
            }
        }

        return if (list.isNotEmpty()) Settings(*list.toTypedArray()) else Settings.empty
    }


    private fun buildNestedSettingTo(container: ArrayList<Setting>, name: String) {
        val settings = build()
        if (settings.isNotEmpty) container += Setting(name, settings)
    }

    
    class SuchSettingAlreadyExistsException (message: String) : IllegalArgumentException(message)
    class NoSuchSettingException (message: String) : IllegalArgumentException(message)

}
