package com.github.kornilova_l.util

import java.text.DecimalFormat
import java.util.Collections
import java.util.Timer
import java.util.TimerTask

class ProgressBar(private val total: Int) {
    private val startTime = System.currentTimeMillis()
    private val timer: Timer = Timer()
    private var lastUpdateTime: Long = 0
    private var current = 0

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                updateProgress(0)
            }
        }, 0, 1000)
    }

    @Synchronized
    fun finish() {
        timer.cancel()
        updateProgress(total - current)
        println()
    }

    @Synchronized
    fun updateProgress(addToProgress: Int) {
        if (addToProgress == 0 && (System.currentTimeMillis() - lastUpdateTime) / 1000 == 0L) {
            return
        }
        current += addToProgress
        lastUpdateTime = System.currentTimeMillis()
        var percent = current * 100 / total
        percent /= 2
        val resizingTotal = 50
        val string = StringBuilder(140)
        percent = if (percent == 0) 1 else percent
        val format = DecimalFormat("####")
        string.append('\r')
                .append(Collections.nCopies(2 - Math.log10((percent * 2).toDouble()).toInt(), " ").joinToString(""))
                .append(String.format(" %d%% [", percent * 2))
                .append(Collections.nCopies(percent, "=").joinToString(""))
                .append('>')
                .append(Collections.nCopies(resizingTotal - percent, " ").joinToString(""))
                .append(']')
                .append(String.format("%4s", format.format((lastUpdateTime - startTime).toDouble() / 1000)))
                .append("s")

        print(string)
    }
}
