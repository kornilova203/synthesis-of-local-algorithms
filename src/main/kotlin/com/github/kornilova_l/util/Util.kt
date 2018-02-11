package com.github.kornilova_l.util

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.TERMINATE
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes

object Util {
    fun deleteDir(path: Path) {
        Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                Files.delete(file)
                return CONTINUE
            }

            override fun visitFileFailed(file: Path, e: IOException?): FileVisitResult {
                return handleException(e!!)
            }

            private fun handleException(e: IOException): FileVisitResult {
                e.printStackTrace() // replace with more robust error handling
                return TERMINATE
            }

            @Throws(IOException::class)
            override fun postVisitDirectory(dir: Path, e: IOException?): FileVisitResult {
                if (e != null) return handleException(e)
                Files.delete(dir)
                return CONTINUE
            }
        })
    }
}