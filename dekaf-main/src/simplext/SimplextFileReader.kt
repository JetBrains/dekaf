package org.jetbrains.dekaf.main.simplext

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 *
 */
class SimplextFileReader<N> : SimplextReader<N> {

    constructor(rootNode: N, handler: (SimplextLine<N>) -> N) : super(rootNode, handler)


    fun processFile(file: Path, charSet: Charset? = null) {
        require(Files.exists(file)) { "File $file doesn't exist" }
        require(Files.isReadable(file)) { "File $file is not readable" }
        Files.lines(file, charSet ?: StandardCharsets.UTF_8).use { stream ->
            processStream(stream)
        }
    }


    fun processStream(stream: Stream<String>) {
        var offset = 0
        for (line in stream) {
            this.bufferOffset = offset
            val len = line.length
            processLine(line, 0, len)
            offset += len + 1
        }
    }

}