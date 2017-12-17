package com.github.kornilova_l.algorithm_synthesis.grid2D

import gnu.trove.list.array.TLongArrayList
import gnu.trove.set.hash.TLongHashSet

private val combinationsCount = Math.pow(2.toDouble(), 30.toDouble()).toLong()

fun main(args: Array<String>) {
    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport()
}

fun countSolvable(solvable: TLongArrayList): Long {
    return countSolvableRecursively(solvable, solvable.size() - 1, 30)
}

internal fun countSolvableRecursively(solvable: TLongArrayList, last: Int, digitsCount: Int): Long {
    if (last == 0) {
        return Math.pow(2.toDouble(), countZeros(solvable[0], digitsCount).toDouble()).toLong()
    }
    val positionsWhereOneChangedToZero = getPositionsWhereOneChangedToZero(solvable, last, digitsCount)
    val partOfSolvable = TLongHashSet()
    for (i in 0 until last) {
        var currentPart = 0L
        var posIndex = 0
        for (position in positionsWhereOneChangedToZero) {
            if (solvable[i].and(1L.shl(position)) > 0) {
                currentPart = currentPart.or(1L.shl(posIndex))
            }
            posIndex++
        }
        partOfSolvable.add(currentPart)
    }
    /* Following is kinda complicated but I am too lazy to comment it */
    return countSolvableRecursively(solvable, last - 1, digitsCount) +
            (Math.pow(2.toDouble(), positionsWhereOneChangedToZero.size.toDouble()).toInt() -
            countSolvableRecursively(TLongArrayList(partOfSolvable), partOfSolvable.size() - 1, positionsWhereOneChangedToZero.size)) *
                    Math.pow(2.toDouble(), (countZeros(solvable[last], digitsCount) - positionsWhereOneChangedToZero.size).toDouble()).toLong()
}

fun getPositionsWhereOneChangedToZero(solvable: TLongArrayList, i: Int, digitsCount: Int): Set<Int> {
    val res = HashSet<Int>()
    val currentS = solvable[i]
    for (j in 0 until i) {
        val s = solvable[j]
        for (index in 0 until digitsCount) {
            if (s.and(1L.shl(index)) > 0 && currentS.and(1L.shl(index)) == 0L) {
                res.add(index)
            }
        }
    }
    return res
}

fun countZeros(number: Long, digitsCount: Int): Int {
    return digitsCount - countOnes(number, digitsCount)
}

fun countOnes(number: Long, digitsCount: Int): Int {
    var onesCount = 0
    for (i in 0 until digitsCount) {
        if (number.and(1L.shl(i)) > 0) {
            onesCount++
        }
    }
    return onesCount
}

fun countUnsolvable(unsolvable: TLongArrayList): Long {
    return 0
}

class ReportPrinter(private val solvable: TLongArrayList, private val unsolvable: TLongArrayList) {
    fun printReport() {
        val solvableCount = countSolvable(solvable)
        val unsolvableCount = countUnsolvable(unsolvable)
        println("Solvable problems count: $solvableCount")
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}