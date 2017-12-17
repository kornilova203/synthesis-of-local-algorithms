package com.github.kornilova_l.algorithm_synthesis.grid2D

import gnu.trove.list.array.TLongArrayList
import gnu.trove.set.hash.TLongHashSet

private val rulesCount = 30
private val combinationsCount = Math.pow(2.toDouble(), rulesCount.toDouble()).toLong()

fun main(args: Array<String>) {
    ReportPrinter(parseLongs(solvableFile), parseLongs(unsolvableFile)).printReport()
}

fun countSolvable(solvable: TLongArrayList): Long {
    return countSolvableRecursively(solvable, solvable.size() - 1, rulesCount)
}

internal fun countSolvableRecursively(solvable: TLongArrayList, last: Int, digitsCount: Int): Long {
    if (last == 0) {
        /* count how many combinations exist if we change some zeros to one */
        return Math.pow(2.toDouble(), countZeros(solvable[0], digitsCount).toDouble()).toLong()
    }
    /* we are interested only in such zeros that are ones in any other solvable */
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

private fun countZeros(number: Long, digitsCount: Int): Int {
    return digitsCount - countOnes(number)
}

private fun countOnes(number: Long): Int {
    return java.lang.Long.bitCount(number)
}

fun countUnsolvable(unsolvable: TLongArrayList): Long {
    val startTime = System.currentTimeMillis()
    val res = countUnsolvableRecursively(unsolvable, unsolvable.size() - 1, rulesCount)
    println(System.currentTimeMillis() - startTime)
    return res
}

fun countUnsolvableRecursively(unsolvable: TLongArrayList, last: Int, digitsCount: Int): Long {
    if (last == 0) {
        return Math.pow(2.toDouble(), countOnes(unsolvable[0]).toDouble()).toLong()
    }
    val positionsWhereZeroChangedToOne = getPositionsWhereZeroChangedToOne(unsolvable, last, digitsCount)
    val positionsCount = countPositions(positionsWhereZeroChangedToOne)
    val partOfUnsolvable = TLongHashSet()
    for (i in 0 until last) {
        var currentPart = 0L
        var posIndex = 0
        for (position in positionsWhereZeroChangedToOne) {
            if (position == -1) {
                break
            }
            if (unsolvable[i].and(1L.shl(position)) > 0) {
                currentPart = currentPart.or(1L.shl(posIndex))
            }
            posIndex++
        }
        partOfUnsolvable.add(currentPart)
    }
    /* Following is kinda complicated but I am too lazy to comment it */
    return countUnsolvableRecursively(unsolvable, last - 1, digitsCount) +
            (Math.pow(2.toDouble(), positionsCount.toDouble()).toInt() -
                    countUnsolvableRecursively(TLongArrayList(partOfUnsolvable), partOfUnsolvable.size() - 1, positionsCount)) *
                    Math.pow(2.toDouble(), (countOnes(unsolvable[last]) - positionsCount).toDouble()).toLong()
}

fun countPositions(positions: IntArray): Int {
    for (i in 0 until positions.size) {
        if (positions[i] == -1) {
            return i
        }
    }
    throw IllegalArgumentException("Array does not contain -1")
}

private fun getPositionsWhereZeroChangedToOne(unsolvable: TLongArrayList, i: Int, digitsCount: Int): IntArray {
    val res = IntArray(digitsCount + 1)
    val currentS = unsolvable[i]
    var allZeros = 0L
    for (j in 0 until i) {
        allZeros = allZeros.and(unsolvable[j])
    }
    var currentArrayIndex = 0
    for (index in 0 until digitsCount) {
        if (allZeros.and(1L.shl(index)) == 0L && currentS.and(1L.shl(index)) > 0) {
            res[currentArrayIndex] = index
            currentArrayIndex++
        }
    }
    res[currentArrayIndex] = -1 // end
    return res
}

class ReportPrinter(private val solvable: TLongArrayList, private val unsolvable: TLongArrayList) {
    fun printReport() {
        val solvableCount = countSolvable(solvable)
        println("Solvable problems count: $solvableCount")
        val unsolvableCount = countUnsolvable(unsolvable)
        println("Unsolvable problems count: $unsolvableCount")
        println("Unknown: ${combinationsCount - solvableCount - unsolvableCount}")
    }
}