package com.github.kornilova_l.util

import oshi.SystemInfo
import java.io.IOException
import java.lang.management.ManagementFactory
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.FileVisitResult.TERMINATE
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.text.NumberFormat

object Util {
    private val runtime = Runtime.getRuntime()
    private val format = NumberFormat.getInstance()
    private val hardware = SystemInfo().hardware

    fun deleteDir(path: Path) {
        if (!path.toFile().exists()) {
            return
        }
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

    fun printMemoryStatus() {
        val maxMemory = runtime.maxMemory()
        val allocatedMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()

        val bytesInKByte = 1024

        println("Max memory available to JVM: ${format.format(maxMemory / bytesInKByte)}K")
        println("Total free memory in JVM:    ${format.format((freeMemory + (maxMemory - allocatedMemory)) / bytesInKByte)}K")
        val heapMemoryUsage = ManagementFactory.getMemoryMXBean().heapMemoryUsage
        println("Heap memory used:            ${format.format(heapMemoryUsage.used / bytesInKByte)}K")
        println("Non-heap memory available:   ${format.format(hardware.memory.available / 1024)}K")
    }

    /**
     * @return one-line string that contains
     * non-heap memory used and total free JVM memory
     */
    fun getBasicMemoryInfo(): String {
        val maxMemory = runtime.maxMemory()
        val allocatedMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()

        return "non-heap: ${format.format(hardware.memory.available / 1024)}K | " +
                "heap: ${format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024)}K"
    }
}