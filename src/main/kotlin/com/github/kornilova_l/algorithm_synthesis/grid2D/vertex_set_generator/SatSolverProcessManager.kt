package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator

import java.io.BufferedWriter
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.util.*


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
    }

    init {
        val builder = ProcessBuilder(File("c_sat/sat").toString())
        builder.redirectErrorStream(true)
        process = builder.start()
        writer = BufferedWriter(OutputStreamWriter(process.outputStream))
        scanner = Scanner(process.inputStream)
        val line = scanner.nextLine()
        if (line != "HELLO") {
            throw RuntimeException("Cannot start python process")
        }
    }

    /**
     * @return null if not satisfiable
     */
    fun solveWithSatSolver(clauses: Set<Set<Int>>, varCount: Int): List<Int>? {
        try {
            writer.write("p cnf $varCount ${clauses.size}\n")
            for (clause in clauses) {
                clause.forEach { variable -> writer.write("$variable") }
                writer.write("e\n")
            }
            writer.flush()
            val line = scanner.nextLine()
            if (line == "RESULT:") {
                return parseResult(scanner)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return null
    }
}