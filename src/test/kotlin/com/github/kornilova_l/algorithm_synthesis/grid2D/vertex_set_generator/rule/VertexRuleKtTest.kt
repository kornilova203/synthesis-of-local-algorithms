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
        var rules = patternToProblem("00000")
        assertEquals(1, rules.size)
        assertEquals(VertexRule("-"), rules.first())


        rules = patternToProblem("01001")
        assertEquals(1, rules.size)
        assertEquals(VertexRule("NW"), rules.first())

        rules = patternToProblem("?1001")
        assertEquals(hashSetOf(VertexRule("NW"), VertexRule("XNW")), rules)

        rules = patternToProblem("??001")
        assertEquals(hashSetOf(
                VertexRule("NW"),
                VertexRule("XNW"),
                VertexRule("XW"),
                VertexRule("W")
        ), rules)

        rules = patternToProblem("?????")
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

    @Test
    fun parseRulesTest() {
        var actual = parseProblem("[X, NWE]")
        assertEquals(hashSetOf(VertexRule("X"), VertexRule("NWE")), actual)

        actual = parseProblem("- E]")
        assertEquals(hashSetOf(VertexRule("-"), VertexRule("E")), actual)
    }

    @Test
    fun rulesToIdTest() {
        var rules: Set<VertexRule> = hashSetOf(VertexRule("XN"), VertexRule("NSW"), VertexRule("S"))
        var setId = problemToId(rules)
        var rulesAgain = idToProblem(setId)
        assertEquals(rules, rulesAgain)

        rules = hashSetOf()
        setId = problemToId(rules)
        rulesAgain = idToProblem(setId)
        assertEquals(rules, rulesAgain)

        rules = hashSetOf(VertexRule("E"), VertexRule("NSWE"), VertexRule("X"))
        setId = problemToId(rules)
        rulesAgain = idToProblem(setId)
        assertEquals(rules, rulesAgain)

        rules = parseProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW")
        setId = problemToId(rules)
        rulesAgain = idToProblem(setId)
        assertEquals(rules, rulesAgain)
    }

    @Test
    fun getNextRuleIdTest() {
        val allowedRules = hashSetOf(VertexRule("N"), VertexRule("S"), VertexRule("XSW"))
        val setId = problemToId(allowedRules)

        assertEquals(problemToId(hashSetOf(VertexRule("S"), VertexRule("XSW"))),
                getNextProblemId(setId, allowedRules))

        assertEquals(problemToId(hashSetOf(VertexRule("XSW"), VertexRule("N"))),
                getNextProblemId(problemToId(hashSetOf(VertexRule("S"), VertexRule("XSW"))), allowedRules))

        val problem = parseProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW")
        val subproblemsCount = Math.pow(2.toDouble(), problem.size.toDouble()).toLong()
        var actualSubproblemsCount = 0L

        var problemId: Int? = problemToId(problem)
        while (problemId != null) {
            problemId = getNextProblemId(problemId, problem)
            actualSubproblemsCount++
        }
        assertEquals(subproblemsCount, actualSubproblemsCount)
    }
}