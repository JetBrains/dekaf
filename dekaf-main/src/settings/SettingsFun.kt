package org.jetbrains.dekaf.main.settings

import org.jetbrains.dekaf.inter.settings.Setting
import org.jetbrains.dekaf.inter.settings.Settings
import java.io.Serializable


fun settingsOf(vararg pairs: Pair<String, Serializable>): Settings {
    val n = pairs.size
    if (n == 0) return Settings.empty
    val a = Array<Setting>(n) {  index ->
        val p = pairs[index]
        Setting(p.first, p.second)
    }
    return Settings(*a)
}


val Setting.pair: Pair<String, Serializable>
    inline get() = Pair(name, value)

