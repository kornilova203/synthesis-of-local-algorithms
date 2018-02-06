package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule


abstract class Problem<out T : VertexRule>(val rules: Set<T>) : Cloneable {

    abstract fun reverse(): Problem<T>

    abstract fun rotate(rotationsCount: Int = 1): Problem<T>

    abstract fun getId(): Int

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (rule in rules) {
            stringBuilder.append("$rule ")
        }
        return stringBuilder.toString()
    }

    override fun hashCode(): Int {
        return rules.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is Problem<*>) {
            return rules == other.rules
        }
        return false
    }
}