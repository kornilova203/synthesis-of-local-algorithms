package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization


class RowDrawer(labels: List<String>, row: BooleanArray) :
        GridDrawer(labels, listOf(""), Array(row.size, { i -> BooleanArray(1, { row[i] }) })) {
    override fun getImageHeight(maxHorizontalLabelWidth: Int): Int = grid.size * (cellWidth + gap) + imagePadding * 2
}