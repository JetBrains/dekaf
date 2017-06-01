package org.jetbrains.dekaf.crazy

import java.nio.file.Paths


object Gauder {

    val dictionary = Dictionary()
    val model      = Model()
    val contriver  = Contriver(model, dictionary)
    val scripter   = Scripter(model)


    @JvmStatic
    fun main(args: Array<String>) {
        dictionary.init("meta/crazy")
        contriver.invent()
        val script = scripter.generate()
        scripter.writeToFile(Paths.get("crazy-script.sql"), script)
    }


}

