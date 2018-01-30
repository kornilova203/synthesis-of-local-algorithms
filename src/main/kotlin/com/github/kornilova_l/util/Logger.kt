package com.github.kornilova_l.util

import java.io.File


class Logger(private val file: File) {
    init {
        file.createNewFile()
        println("Log file: ${file.absolutePath}")
    }

    fun info(message: String) {
        file.appendText(message)
        file.appendText("\n")
        println(message)
    }

    fun error(e: Throwable) {
        file.appendText(e.stackTrace.toString())
        e.printStackTrace()
    }
}