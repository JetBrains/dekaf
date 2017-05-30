package org.jetbrains.dekaf.crazy

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*


class Dictionary {

    lateinit var dir: Path

    val nouns      = TreeSet<String>(String.CASE_INSENSITIVE_ORDER)
    val adjectives = TreeSet<String>(String.CASE_INSENSITIVE_ORDER)

    fun init(dirPath: String) {
        dir = Paths.get(dirPath)
        assert(Files.exists(dir)) { "Dictionary directory $dir doesn't exist" }
        assert(Files.isDirectory(dir)) { "Dictionary path $dir is not a directory" }

        loadWords("nouns", nouns)
        loadWords("adjectives", adjectives)
    }

    private fun loadWords(name: String, collection: MutableCollection<String>) {
        val fileName = name + ".txt"
        val file = dir.resolve(fileName)
        assert(Files.exists(file)) { "Dictionary file $fileName doesn't exist" }
        assert(Files.isRegularFile(file)) { "Dictionary file $fileName is not a text file" }

        Files.lines(file, Charsets.UTF_8)
             .map { it.trim() }
             .filter { it.length >= 2 }
             .forEach { collection.add(it) }

        val n = collection.size
        if (n == 0) throw IllegalStateException("No $name!")
        if (n < 10) throw IllegalStateException("Too few $name (just $n words)!")
        System.out.println("Loaded $n $name")
    }


}
