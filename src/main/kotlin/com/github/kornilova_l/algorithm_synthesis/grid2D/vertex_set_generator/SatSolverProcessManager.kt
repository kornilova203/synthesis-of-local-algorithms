package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import gnu.trove.list.array.TIntArrayList
import jnisat.JPicoSat

/**
 * @return null if not satisfiable
 */
fun isSolvable(clauses: TIntArrayList): Boolean {
    val sat = initSat(clauses)
    return sat.solve()
}

/**
 * @return null if not satisfiable
 * @param clauses a list of ints. Clauses are separated by 0
 */
fun solve(clauses: TIntArrayList, varCount: Int): List<Int>? {
    val sat = initSat(clauses)
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
            else -> throw RuntimeException("Variable cannot be found in sat")
        }
    }
    return solution
}

private fun initSat(clauses: TIntArrayList): JPicoSat {
    val sat = JPicoSat()
    var startIndex = 0
    for (i in 0 until clauses.size()) {
        if (clauses[i] == 0) {
            sat.addClause(*clauses.subList(startIndex, i).toArray())
            startIndex = i + 1
        }
    }
    if (clauses[clauses.size() - 1] != 0) { // if there is no last zero
        sat.addClause(startIndex, clauses.size())
    }
    return sat
}