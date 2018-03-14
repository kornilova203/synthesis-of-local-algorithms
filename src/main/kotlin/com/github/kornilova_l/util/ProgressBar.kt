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
    private val barLength = 50
    private val floatingPointFormat = DecimalFormat("##.##")
    @Volatile
    private var wasFinished = false

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
        val percent = current.get().toDouble() * 100 / total
        val string = StringBuilder(140)
        val timePassed = lastUpdateTime.get() - startTime.get()
        val filledLength = Math.ceil(barLength * (percent / 100)).toInt()
        string.append('\r')
                .append(title)
                .append(String.format("%5s", floatingPointFormat.format(percent)))
                .append("% [")
                .append(Collections.nCopies(filledLength, "=").joinToString(""))
                .append('>')
                .append(Collections.nCopies(barLength - filledLength, " ").joinToString(""))
                .append("] ")
                .append(msToPrettyString(timePassed))

        if (timePassed / 1000 > 0 && current.get() > 0) { // predict time
            val timePerElement = timePassed.toDouble() / current.get()
            val totalTime = timePerElement * total
            string
                    .append(" | ")
                    .append(msToPrettyString(totalTime.toLong()))
                    .append(" | ")
                    .append(String.format("%-70s", Util.getBasicMemoryInfo()))
        }
        print(string)
    }

    private fun msToPrettyString(timePassed: Long): String {
        val sec = timePassed / 1000
        val stringBuilder = StringBuilder()
        stringBuilder
                .append(String.format("%2s", sec / 60 / 60))
                .append("h ")
                .append(String.format("%2s", sec / 60 % 60))
                .append("m ")
                .append(String.format("%2s", sec % 60))
                .append("s")
        return stringBuilder.toString()
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
