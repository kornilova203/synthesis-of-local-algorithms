package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem


abstract class VertexRule {
    protected abstract val array: BooleanArray
    abstract val id: Int

    abstract fun rotate(rotationsCount: Int = 1): VertexRule

    abstract fun toHumanReadableSting(): String

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
        fun setArrayValues(id: Int, array: BooleanArray): BooleanArray {
            (0 until array.size).filter { getBit(id, it) }
                    .forEach { array[it] = true }
            return array
        }
    }
}