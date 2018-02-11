package com.github.kornilova_l.util

import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class ProgressBar(private val total: Int, private val title: String = "") {
    private val startTime = AtomicLong(System.currentTimeMillis())
    private val timer: Timer = Timer()
    private val lastUpdateTime = AtomicLong()
    private val current = AtomicInteger()
    @Volatile private var wasFinished = false

    init {
        timer.schedule(object : TimerTask() {
            override fun run() {
                updateProgress(0)
                redraw()
            }
        }, 0, 1000)
    }

    fun finish() {
        if (wasFinished) {
            return
        }
        wasFinished = true
        timer.cancel()
        updateProgress(total - current.get())
        redraw()
        println()
    }

    @Synchronized
    private fun redraw() {
        var percent = current.get().toDouble() * 100 / total
        percent /= 2
        val resizingTotal = 50
        val string = StringBuilder(140)
        percent = if (percent == 0.toDouble()) 0.01 else percent
        val intPercent = Math.ceil(percent).toInt()
        val format = DecimalFormat("####")
        val timePassed = lastUpdateTime.get() - startTime.get()
        string.append('\r')
                .append(title)
                .append(Collections.nCopies(2 - Math.log10((intPercent * 2).toDouble()).toInt(), " ").joinToString(""))
                .append(String.format("%.2f%% [", percent * 2))
                .append(Collections.nCopies(intPercent, "=").joinToString(""))
                .append('>')
                .append(Collections.nCopies(resizingTotal - intPercent, " ").joinToString(""))
                .append(']')
                .append(String.format("%4s", format.format(timePassed / 1000)))
                .append("s")

        if (timePassed / 1000 > 0 && current.get() > 0) { // predict time
            val timePerElement = timePassed.toDouble() / 1000 / current.get()
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

    fun updateProgress(addToProgress: Int = 1) {
        if (addToProgress == 0 && (System.currentTimeMillis() - lastUpdateTime.get()) / 1000 == 0L) {
            return
        }
        current.addAndGet(addToProgress)
        lastUpdateTime.set(System.currentTimeMillis())
        if (current.get() == total) {
            finish()
        }
    }
}
