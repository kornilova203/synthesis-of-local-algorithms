package com.github.kornilova_l.algorithm_synthesis.grid2D.five_neighbours_problems.problem

class FiveNeighboursNonTrivialProblem(rules: Set<FiveNeighboursRule>) : FiveNeighboursProblem(rules) {

    constructor(combinationNum: Int) : this(idToRules(combinationNum))

    constructor(problem: FiveNeighboursProblem) : this(problem.rules)

    /**
     * Check that there is no trivial rule in rules
     */
    init {
        for (rule in rules) {
            if (rule !in allRulesExceptTrivial) {
                throw IllegalArgumentException("Problem must not contain trivial rule $rule")
            }
        }
    }

    /**
     * This method is used to iterate through non-trivial problems
     * @throws IllegalArgumentException if problem is trivial
     */
    fun getId(): Int {
        var setId = 0
        for (rule in rules) {
            var ruleId = -1
            for (i in 0 until allRulesExceptTrivial.size) {
                if (rule == allRulesExceptTrivial[i]) {
                    ruleId = i
                    break
                }
            }
            if (ruleId == -1) {
                throw IllegalArgumentException("Problem is trivial")
            }
            setId = setId.or(1.shl(ruleId))
        }
        return setId
    }
}