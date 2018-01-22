package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

fun main(args: Array<String>) {
    RowDrawer(listOf("0, 1", "1, 2, 3", "0", "1"), booleanArrayOf(true, false, true, true)).outputImage()
}

class RowDrawer(labels: List<String>, row: BooleanArray) :
        GridDrawer(labels, listOf(""), Array(row.size, { i -> BooleanArray(1, { row[i] }) })) {
    override fun getImageHeight(maxHorizontalLabelWidth: Int): Int = grid.size * (cellWidth + gap) + imagePadding * 2
}