package org.jetbrains.dekaf.crazy


object Gauder {

    val dictionary = Dictionary()
    val model = Model()
    val contriver = Contriver(model, dictionary)


    @JvmStatic
    fun main(args: Array<String>) {
        dictionary.init("meta/crazy")
        contriver.invent()
    }


}

