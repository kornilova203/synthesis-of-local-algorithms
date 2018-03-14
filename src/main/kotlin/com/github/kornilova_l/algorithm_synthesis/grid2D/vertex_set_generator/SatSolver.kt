package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import jnisat.JPicoSat


class SatSolver {
    private val sat = JPicoSat()
    private var isSolved = false

    fun addClause(clause: IntArray) {
        if (isSolved) {
            throw IllegalArgumentException("Solve method was already called")
        }
        sat.addClause(*clause)
    }

    fun addClause(value: Int) {
        if (isSolved) {
            throw IllegalArgumentException("Solve method was already called")
        }
        sat.addClause(value)
    }

    fun addClause(value1: Int, value2: Int) {
        if (isSolved) {
            throw IllegalArgumentException("Solve method was already called")
        }
        sat.addClause(value1, value2)
    }

    fun solve(varCount: Int): List<Int>? {
        val res = sat.solve()
        if (!res) {
            reset()
            return null
        }
        val solution = ArrayList<Int>()
        for (variable in 1..varCount) {
            val value = sat.getValue(variable)
            when {
                value < 0 -> solution.add(-variable)
                value > 0 -> solution.add(variable)
                else -> solution.add(-variable) // variable may be any
            }
        }
        reset()
        return solution
    }

    /**
     * Remove sat variables from memory.
     * It seems like gc is not fast enough to finalize all sat solvers
     * therefore we need to call reset beforehand
     * without this reset() native code consumes all memory */
    private fun reset() {
        sat.reset()
        isSolved = true
    }

    fun isSolvable(): Boolean {
        val res = sat.solve()
        reset()
        return res
    }
}