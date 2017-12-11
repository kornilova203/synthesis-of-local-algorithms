package com.github.kornilova_l.algorithm_synthesis.grid2D

import com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule.VertexRule
import gnu.trove.list.array.TLongArrayList
import org.junit.Assert.*
import org.junit.Test

class CheckAllKtTest {

    @Test
    fun isUnsolvableTest() {
        val unsolvable = TLongArrayList()
        unsolvable.add(15)
        assertTrue(isUnsolvable(7, unsolvable))

        unsolvable.clear()
        unsolvable.add(15)
        assertFalse(isUnsolvable(31, unsolvable))
    }

    @Test
    fun isSolvableTest() {
        val solvable = TLongArrayList()
        solvable.add(15)
        assertFalse(isSolvable(7, solvable))

        solvable.clear()
        solvable.add(7)
        assertTrue(isSolvable(15, solvable))

        solvable.clear()
        solvable.add(8)
        assertTrue(isSolvable(9, solvable))
    }

    @Test
    fun toSetOfVertexRulesTest() {
        val rules = toSetOfVertexRules(7)
        assertEquals(hashSetOf(VertexRule("-"), VertexRule("N"), VertexRule("E")), rules)
    }
}