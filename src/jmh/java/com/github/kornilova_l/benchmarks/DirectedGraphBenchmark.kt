package com.github.kornilova_l.benchmarks

import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.DirectedGraph
import com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections.TileSet
import org.openjdk.jmh.annotations.*
import java.io.File
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 2)
@Measurement(iterations = 8)
open class DirectedGraphBenchmark {

    var tiles: TileSet? = null

    @Setup
    fun doSetup() {
        tiles = TileSet(File("generated_tiles/6-7-1.txt"))
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.SECONDS)
    fun directedGraphBuild() {
        DirectedGraph(tiles!!)
    }
}
