package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import jnisat.JPicoSat
import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*
import kotlin.collections.ArrayList


class SatSolverProcessManager private constructor() {
    private val process: Process
    private val writer: BufferedWriter
    private val scanner: Scanner


    companion object {
        // it will not be instantiated until it is used
        // because there is no other public static method or public fields
        var satManager = SatSolverProcessManager()

        init {
            println("Sat Manager is instantiated")
        }

        /**
         * @return null if not satisfiable
         */
        fun solve(clauses: Set<Set<Int>>, varCount: Int): List<Int>? {
            val sat = JPicoSat()
            for (clause in clauses) {
                sat.addClause(*clause.toIntArray())
            }
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
    }

    /**
     * @return null if not satisfiable
     */
    fun solveWithSatSolver(clauses: Set<Set<Int>>, varCount: Int): List<Int>? {
        try {
            writer.write("p cnf $varCount ${clauses.size}\n")
            for (clause in clauses) {
                for (variable in clause) {
                    writer.write("$variable ")
                }
                writer.write("\n")
            }
            writer.write("END\n")
            writer.flush()
            val line = scanner.nextLine()
            if (line == "RESULT:") {
                val res = parseResult(scanner)
                return res
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }

    init {
        val builder = ProcessBuilder("python", File("python_sat/sat/start_sat_iterative.py").toString())
        builder.redirectErrorStream(true)
        process = builder.start()
        writer = BufferedWriter(OutputStreamWriter(process.outputStream))
        scanner = Scanner(process.inputStream)
        val line = scanner.nextLine()
        if (line != "HELLO") {
            throw RuntimeException("Cannot start python process")
        }
    }
}