@file:JvmName("FileFun")

package org.jetbrains.dekaf.test.utils

import java.nio.file.Files
import java.nio.file.Path


fun Path.exists() = Files.exists(this)

fun Path.isDirectory() = Files.isDirectory(this)

fun Path.isFile() = Files.isRegularFile(this)

fun Path.isReadable() = Files.isReadable(this)

