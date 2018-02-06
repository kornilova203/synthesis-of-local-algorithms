package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.problem

import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.*
import com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem.FiveNeighboursProblem.Companion.parseProblem
import org.junit.Assert.*
import org.junit.Test

class FiveNeighboursRuleKtTest {
    @Test
    fun toHumanReadableStingTest() {
        assertEquals("X", FiveNeighboursRule(1).toHumanReadableSting())
        assertEquals("N", FiveNeighboursRule(2).toHumanReadableSting())
        assertEquals("XN", FiveNeighboursRule(3).toHumanReadableSting())
    }

    @Test
    fun isIncludedTest() {
        assertTrue(FiveNeighboursRule(31).isIncluded(FIVE_POSITION.X))
        assertTrue(FiveNeighboursRule(31).isIncluded(FIVE_POSITION.N))
        assertTrue(FiveNeighboursRule(31).isIncluded(FIVE_POSITION.E))
        assertTrue(FiveNeighboursRule(31).isIncluded(FIVE_POSITION.S))
        assertTrue(FiveNeighboursRule(31).isIncluded(FIVE_POSITION.W))

        assertTrue(FiveNeighboursRule(1).isIncluded(FIVE_POSITION.X))
        assertFalse(FiveNeighboursRule(1).isIncluded(FIVE_POSITION.N))
    }

    @Test
    fun reverseRulesTest() {
        assertEquals(2, hashSetOf(FiveNeighboursRule(1), FiveNeighboursRule(2), FiveNeighboursRule(1)).size)
        assertEquals(30, FiveNeighboursProblem(hashSetOf(FiveNeighboursRule(1), FiveNeighboursRule(2), FiveNeighboursRule(1))).reverse().rules.size)
    }

    @Test
    fun setFromPatternTest() {
        var problem = FiveNeighboursProblem("00000")
        assertEquals(1, problem.rules.size)
        assertEquals(FiveNeighboursRule("-"), problem.rules.first())


        problem = FiveNeighboursProblem("01001")
        assertEquals(1, problem.rules.size)
        assertEquals(FiveNeighboursRule("NW"), problem.rules.first())

        problem = FiveNeighboursProblem("?1001")
        assertEquals(hashSetOf(FiveNeighboursRule("NW"), FiveNeighboursRule("XNW")), problem.rules)

        problem = FiveNeighboursProblem("??001")
        assertEquals(hashSetOf(
                FiveNeighboursRule("NW"),
                FiveNeighboursRule("XNW"),
                FiveNeighboursRule("XW"),
                FiveNeighboursRule("W")
        ), problem.rules)

        problem = FiveNeighboursProblem("?????")
        assertEquals(32, problem.rules.size)
    }

    @Test
    fun rotateRuleTest() {
        var rule = FiveNeighboursRule("XN")
        assertEquals(FiveNeighboursRule("XE"), rule.rotate())

        rule = FiveNeighboursRule("NESW")
        assertEquals(FiveNeighboursRule("ENWS"), rule.rotate())

        rule = FiveNeighboursRule("XN")
        assertEquals(FiveNeighboursRule("XS"), rule.rotate(2))

        rule = FiveNeighboursRule("XN")
        assertEquals(FiveNeighboursRule("XW"), rule.rotate(-1))
    }

    @Test
    fun parseRulesTest() {
        var actual = parseProblem("[X, NWE]")
        assertEquals(hashSetOf(FiveNeighboursRule("X"), FiveNeighboursRule("NWE")), actual.rules)

        actual = parseProblem("- E]")
        assertEquals(hashSetOf(FiveNeighboursRule("-"), FiveNeighboursRule("E")), actual.rules)
    }

    @Test
    fun rulesToIdTest() {
        var problem = FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("XN"), FiveNeighboursRule("NSW"), FiveNeighboursRule("S")))
        var setId = problem.getId()
        var rulesAgain = FiveNeighboursProblem(setId)
        assertEquals(problem, rulesAgain)

        problem = FiveNeighboursNonTrivialProblem(hashSetOf())
        setId = problem.getId()
        rulesAgain = FiveNeighboursProblem(setId)
        assertEquals(problem, rulesAgain)

        problem = FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("E"), FiveNeighboursRule("NSWE"), FiveNeighboursRule("X")))
        setId = problem.getId()
        rulesAgain = FiveNeighboursProblem(setId)
        assertEquals(problem, rulesAgain)

        problem = FiveNeighboursNonTrivialProblem(parseProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW"))
        setId = problem.getId()
        rulesAgain = FiveNeighboursProblem(setId)
        assertEquals(problem, rulesAgain)
    }

    @Test
    fun getNextRuleIdTest() {
        val allowedRules = FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("N"), FiveNeighboursRule("S"), FiveNeighboursRule("XSW")))
        val setId = allowedRules.getId()

        assertEquals(FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("S"), FiveNeighboursRule("XSW"))).getId(),
                getNextProblemId(setId, allowedRules))

        assertEquals(FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("XSW"), FiveNeighboursRule("N"))).getId(),
                getNextProblemId(FiveNeighboursNonTrivialProblem(hashSetOf(FiveNeighboursRule("S"), FiveNeighboursRule("XSW"))).getId(), allowedRules))

        val problem = FiveNeighboursNonTrivialProblem(parseProblem("XN, XE, NE, XNE, XS, NS, XNS, ES, XES, NES, XNES, XW, NW, XNW, EW, XEW, NEW, XNEW, SW, XSW, NSW, XNSW, ESW, XESW"))
        val subproblemsCount = Math.pow(2.toDouble(), problem.rules.size.toDouble()).toLong()
        var actualSubproblemsCount = 0L

        var problemId: Int? = problem.getId()
        while (problemId != null) {
            problemId = getNextProblemId(problemId, problem)
            actualSubproblemsCount++
        }
        assertEquals(subproblemsCount, actualSubproblemsCount)
    }
}