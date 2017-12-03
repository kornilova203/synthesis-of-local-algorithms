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
                redraw()
            }
        }, 0, 1000)
    }

    @Synchronized
    fun finish() {
        timer.cancel()
        updateProgress(total - current)
        redraw()
        println()
    }

    fun redraw() {
        var percent = current * 100 / total.toDouble()
        percent /= 2
        val resizingTotal = 50
        val string = StringBuilder(140)
        percent = if (percent == 0.toDouble()) 0.01 else percent
        val intPercent = Math.ceil(percent).toInt()
        val format = DecimalFormat("####")
        val timePassed = lastUpdateTime - startTime
        string.append('\r')
                .append(Collections.nCopies(2 - Math.log10((intPercent * 2).toDouble()).toInt(), " ").joinToString(""))
                .append(String.format("%.2f%% [", percent * 2))
                .append(Collections.nCopies(intPercent, "=").joinToString(""))
                .append('>')
                .append(Collections.nCopies(resizingTotal - intPercent, " ").joinToString(""))
                .append(']')
                .append(String.format("%4s", format.format(timePassed / 1000)))
                .append("s")

        if (timePassed / 1000 > 0 && current > 0) { // predict time
            val timePerElement = timePassed.toDouble() / 1000 / current
            val totalTime = timePerElement * total
            string
                    .append(" / ")
                    .append(String.format("%4s", format.format(totalTime / 60)))
                    .append("m ")
                    .append(format.format(totalTime % 60))
                    .append("s")
        }

        print(string)
    }

    @Synchronized
    fun updateProgress(addToProgress: Int) {
        if (addToProgress == 0 && (System.currentTimeMillis() - lastUpdateTime) / 1000 == 0L) {
            return
        }
        current += addToProgress
        lastUpdateTime = System.currentTimeMillis()
    }
}
