package com.github.kornilova_l.util

import java.io.File


abstract class FileNameCreator {
    fun getFileName(n: Int, m: Int, size: Int): String = getFileName(
            getName(),
            getParameters(n, m, size),
            "tiles"
    )

    protected abstract fun getParameters(n: Int, m: Int, size: Int): Map<String, Int>

    protected abstract fun getName(): String

    companion object {
        fun getFileName(name: String, parameters: Map<String, Int>, extension: String?): String {
            val string = StringBuilder().append("name=").append(name)
            for (parameter in parameters) {
                string.append("_").append(parameter.key).append("=").append(parameter.value)
            }
            if (extension != null) {
                string.append(".").append(extension)
            }
            return string.toString()
        }

        fun getIntParameter(fileName: String, parameterName: String): Int? {
            val searchString = "_$parameterName="
            val index = fileName.indexOf(searchString)
            if (index == -1) {
                return null
            }
            val startPos = index + searchString.length
            for (i in startPos until fileName.length) { // find end
                val c = fileName[i]
                if (c == '_' || c == '.') {
                    return Integer.parseInt(fileName.substring(startPos, i))
                }
            }
            return Integer.parseInt(fileName.substring(startPos, fileName.length))
        }

        fun getExtension(fileName: String): String {
            val lastDot = fileName.lastIndexOf('.')
            if (lastDot == -1) {
                return ""
            }
            return fileName.substring(lastDot + 1, fileName.length)
        }

        fun getFile(dir: File, n: Int, m: Int, k: Int, extension: String? = null): File? =
                getFile(dir, mapOf(Pair("n", n), Pair("m", m), Pair("k", k)), extension)

        fun getFile(dir: File, n: Int, m: Int, extension: String? = null): File? =
                getFile(dir, mapOf(Pair("n", n), Pair("m", m)), extension)

        fun getFile(dir: File, parameters: Map<String, Int>, extension: String? = null): File? {
            val files = dir.listFiles() ?: return null
            for (file in files) {
                if (!file.isFile) {
                    continue
                }
                if (extension != null) {
                    if (getExtension(file.name) != extension) {
                        continue
                    }
                }
                if (parameters.all { parameter -> getIntParameter(file.name, parameter.key) == parameter.value }) {
                    return file
                }
            }
            return null
        }
    }
}