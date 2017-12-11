package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.rule

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

    @Test
    fun setFromPatternTest() {
        var rules = getVertexRules("00000")
        assertEquals(1, rules.size)
        assertEquals(VertexRule("-"), rules.first())


        rules = getVertexRules("01001")
        assertEquals(1, rules.size)
        assertEquals(VertexRule("NW"), rules.first())

        rules = getVertexRules("?1001")
        assertEquals(hashSetOf(VertexRule("NW"), VertexRule("XNW")), rules)

        rules = getVertexRules("??001")
        assertEquals(hashSetOf(
                VertexRule("NW"),
                VertexRule("XNW"),
                VertexRule("XW"),
                VertexRule("W")
        ), rules)

        rules = getVertexRules("?????")
        assertEquals(32, rules.size)
    }

    @Test
    fun rotateRuleTest() {
        var rule = VertexRule("XN")
        assertEquals(VertexRule("XE"), rule.rotate())

        rule = VertexRule("NESW")
        assertEquals(VertexRule("ENWS"), rule.rotate())

        rule = VertexRule("XN")
        assertEquals(VertexRule("XS"), rule.rotate(2))

        rule = VertexRule("XN")
        assertEquals(VertexRule("XW"), rule.rotate(-1))
    }
}