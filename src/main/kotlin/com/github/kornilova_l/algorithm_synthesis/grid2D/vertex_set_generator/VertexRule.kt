package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import org.apache.commons.collections4.BidiMap
import org.apache.commons.collections4.bidimap.DualHashBidiMap
import java.util.*
import kotlin.experimental.and

/**
 * ____1
 * _4__0__2
 * ____3
 *
 * =>
 *
 * ____N
 * _W__X__E
 * ____S
 */

enum class POSITION {
    X,
    N,
    E,
    S,
    W
}

val positionIndexes: BidiMap<POSITION, Int> = DualHashBidiMap()
val positionLetters: BidiMap<POSITION, Char> = DualHashBidiMap()
val positions = hashSetOf(POSITION.X, POSITION.N, POSITION.E, POSITION.S, POSITION.W)

/**
 * @return new set which contains all possible rules but without
 * the rules which are given as a parameter. Size of new set is 32 - rules.size()
 */
fun reverseRules(rules: Set<VertexRule>): Set<VertexRule> {
    val ids = HashSet<Int>() // convert rules to ids for convenient compare
    rules.mapTo(ids) { it.id }

    val reversedRules = HashSet<VertexRule>()
    (0..31).filter { !ids.contains(it) }
            .forEach { reversedRules.add(VertexRule(it)) }
    return reversedRules
}

fun getBit(num: Int, position: Int): Boolean = (num shr position).toByte() and 1 == 1.toByte()

class VertexRule {
    val array = Array(32, { false })
    val id: Int

    constructor(id: Int) {
        this.id = id
        if (id >= 32) {
            throw IllegalArgumentException("Id must be smaller than 32")
        }
        (0..4).filter { getBit(id, it) }
                .forEach { array[it] = true }
    }

    constructor(rule: String) {
        var id = 0
        for (i in 0 until rule.length) {
            val c = rule[i]
            val position = positionLetters.getKey(c)!!
            val index = positionIndexes[position]!!
            array[index] = true
            id += Math.pow(2.toDouble(), index.toDouble()).toInt()
        }
        this.id = id
    }

    fun isIncluded(position: POSITION): Boolean = array[positionIndexes[position]!!]

    fun toHumanReadableSting(): String {
        val stringBuilder = StringBuilder()
        array.indices
                .filter { array[it] }
                .forEach { stringBuilder.append(positionLetters[positionIndexes.getKey(it)]) }
        if (stringBuilder.isEmpty()) {
            return "-"
        }
        return stringBuilder.toString()
    }

    override fun toString(): String = toHumanReadableSting()

    override fun hashCode(): Int = id.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as VertexRule

        if (id != other.id) return false

        return true
    }

    companion object {
        init {
            positionIndexes[POSITION.X] = 0
            positionIndexes[POSITION.N] = 1
            positionIndexes[POSITION.E] = 2
            positionIndexes[POSITION.S] = 3
            positionIndexes[POSITION.W] = 4

            positionLetters[POSITION.X] = 'X'
            positionLetters[POSITION.N] = 'N'
            positionLetters[POSITION.E] = 'E'
            positionLetters[POSITION.S] = 'S'
            positionLetters[POSITION.W] = 'W'
        }
    }
}