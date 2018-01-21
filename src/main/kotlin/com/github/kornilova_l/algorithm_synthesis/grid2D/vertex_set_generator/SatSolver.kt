package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import jnisat.JPicoSat


class SatSolver {
    private val sat = JPicoSat()

    fun addClause(clause: IntArray) {
        sat.addClause(*clause)
    }

    fun addClause(value: Int) {
        sat.addClause(value)
    }

    fun addClause(value1: Int, value2: Int) {
        sat.addClause(value1, value2)
    }

    fun solve(varCount: Int): List<Int>? {
        val res = sat.solve()
        if (!res) {
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
        return solution
    }

    fun isSolvable(): Boolean {
        return sat.solve()
    }
}