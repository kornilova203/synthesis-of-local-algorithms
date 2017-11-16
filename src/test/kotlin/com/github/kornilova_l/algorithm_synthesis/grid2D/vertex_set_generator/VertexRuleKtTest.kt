package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import org.junit.Assert.*
import org.junit.Test

class VertexRuleKtTest {
    @Test
    fun toHumanReadableStingTest() {
        assertEquals("X", VertexRule(1).toHumanReadableSting())
        assertEquals("N", VertexRule(2).toHumanReadableSting())
        assertEquals("XN", VertexRule(3).toHumanReadableSting())
    }

    @Test
    fun isIncludedTest() {
        assertTrue(VertexRule(31).isIncluded(POSITION.X))
        assertTrue(VertexRule(31).isIncluded(POSITION.N))
        assertTrue(VertexRule(31).isIncluded(POSITION.E))
        assertTrue(VertexRule(31).isIncluded(POSITION.S))
        assertTrue(VertexRule(31).isIncluded(POSITION.W))

        assertTrue(VertexRule(1).isIncluded(POSITION.X))
        assertFalse(VertexRule(1).isIncluded(POSITION.N))
    }

    @Test
    fun reverseRulesTest() {
        assertEquals(2, hashSetOf(VertexRule(1), VertexRule(2), VertexRule(1)).size)
        assertEquals(30, reverseRules(hashSetOf(VertexRule(1), VertexRule(2), VertexRule(1))).size)
    }

}