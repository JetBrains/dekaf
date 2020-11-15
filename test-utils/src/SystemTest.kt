package org.jetbrains.dekaf.test.utils

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.stream.Collectors


@Tag("SystemTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
interface SystemTest



interface SystemTestWithTempDir : SystemTest {

    @Suppress("unused")
    companion object {

        //@TempDir @JvmStatic
        var rootTempDir: Path? = null

        @JvmStatic
        @BeforeAll
        fun createRootTemporaryDirectory() {
            val td = Files.createTempDirectory("DekafTesting-")
            assert(Files.exists(td))
            assert(Files.isDirectory(td))
            rootTempDir = td
        }

        @JvmStatic
        @AfterAll
        fun dropRootTemporaryDirectory() {
            val td = rootTempDir ?: return
            deleteTemporaryDirectoryRecursively(td)
        }

        private fun deleteTemporaryDirectoryRecursively(dir: Path) {
            System.out.println("Going to delete the temporary directory: $dir")
            val pathsToDelete =
                    Files.walk(dir)
                        .sorted(Comparator.reverseOrder())
                        .collect(Collectors.toList())

            var dirsCount = 0
            var filesCount = 0
            for (path in pathsToDelete) {
                if (path.isDirectory()) dirsCount++
                if (path.isFile()) filesCount++
            }
            System.out.println("\tit contains $dirsCount sub-directories and $filesCount files")

            for (path in pathsToDelete) {
                Files.deleteIfExists(path)
            }
            System.out.println("\tdone.")
        }


        @JvmStatic
        fun getTempDir(): Path {
            val trace: Array<StackTraceElement> = Thread.currentThread().stackTrace
            assert(trace.size >= 4)
            val entry: StackTraceElement = trace[2]
            val longName = entry.className
            val lastPoint = longName.lastIndexOf('.')
            val shortName = if (lastPoint >= 0) longName.substring(lastPoint + 1) else longName
            return getTempDir(shortName)
        }

        @JvmStatic
        fun getTempDir(name: String): Path {
            val rootTempDir = rootTempDir
                              ?: throw IllegalStateException("The temporary directory was not created yet")
            require(rootTempDir.exists()) { "The temporary directory ($rootTempDir) should exist" }
            require(rootTempDir.isDirectory()) { "The temporary directory ($rootTempDir) should be a directory" }
            val innerDir = rootTempDir.resolve(name)
            Files.createDirectory(innerDir)
            assert(innerDir.exists() && innerDir.isDirectory()) { "Cannot create sub-directory properly" }
            return innerDir
        }

    }
}

