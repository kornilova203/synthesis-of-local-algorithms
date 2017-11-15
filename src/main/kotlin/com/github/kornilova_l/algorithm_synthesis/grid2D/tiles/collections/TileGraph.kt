package com.github.kornilova_l.algorithm_synthesis.grid2D.tiles.collections


abstract class TileGraph(tileSet1: TileSet, tileSet2: TileSet) {
    abstract val edgeCount: Int
    abstract val size: Int
    protected val tileSet1: TileSet
    protected val tileSet2: TileSet
    val n: Int
    val m: Int
    val k: Int

    init {
        validateTileSets(tileSet1, tileSet2)
        val n1 = tileSet1.n
        val m1 = tileSet1.m
        val n2 = tileSet2.n
        val m2 = tileSet2.m
        if (n1 > n2) {
            n = n2
            m = m1
            this.tileSet1 = tileSet1
            this.tileSet2 = tileSet2
        } else {
            n = n1
            m = m2
            this.tileSet1 = tileSet2
            this.tileSet2 = tileSet1
        }

        k = tileSet1.k
    }

    private fun validateTileSets(tileSet1: TileSet, tileSet2: TileSet) {
        if (tileSet1.k != tileSet2.k) {
            throw IllegalArgumentException("Graph power is different in two sets")
        }
        if (tileSet1.isEmpty || tileSet2.isEmpty) {
            throw IllegalArgumentException("At least one set is empty")
        }

        val n1 = tileSet1.n
        val m1 = tileSet1.m
        val n2 = tileSet2.n
        val m2 = tileSet2.m
        if (n1 > n2) {
            if (n1 != n2 + 1 || m2 != m1 + 1) {
                throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
            }
        } else {
            if (n1 + 1 != n2 || m2 + 1 != m1) {
                throw IllegalArgumentException("If size of kern tile is n*m then size of two tiles set must be n+1*m and n*m+1 (order does not matter)")
            }
        }
    }
}