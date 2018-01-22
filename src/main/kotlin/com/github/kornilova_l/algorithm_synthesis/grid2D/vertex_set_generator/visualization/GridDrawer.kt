package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.*
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Draws grid with horizontal and vertical labels
 */
open class GridDrawer(private val verticalLabels: List<String>, private val horizontalLabels: List<String>,
                      protected val grid: Array<BooleanArray>) {
    private val green = Color(52, 192, 119)
    private val font = Font("Arial", 0, 14)
    protected val gap = 3
    protected val imagePadding = 20
    private val gapBetweenLabelsAndGrid = 5

    init {
        if (verticalLabels.size != grid.size) {
            throw IllegalArgumentException("Amount of vertical labels does not match number of rows in grid 2D array")
        }
        if (horizontalLabels.size != grid.first().size) {
            throw IllegalArgumentException("Amount of horizontal labels does not match number of columns in grid 2D array")
        }
    }

    fun outputImage(file: File = File("generated_images/scheme.png")) {
        val maxVerticalLabelWidth = getMaxStringWidth(verticalLabels)
        val maxHorizontalLabelWidth = getMaxStringWidth(horizontalLabels)
        val imageHeight = getImageHeight(maxHorizontalLabelWidth)
        val imageWidth = getImageWidth(maxVerticalLabelWidth)
        val bufferedImage = BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_ARGB)
        val graphics = createGraphics(bufferedImage, imageWidth, imageHeight)
        graphics.font = font
        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        )
        drawVerticalLabels(graphics, verticalLabels, maxVerticalLabelWidth)
        drawHorizontalLabels(graphics, horizontalLabels, maxHorizontalLabelWidth)
        draw(graphics, maxVerticalLabelWidth)
        ImageIO.write(bufferedImage, "png", file)
    }

    protected open fun getImageHeight(maxHorizontalLabelWidth: Int): Int =
            grid.size * (cellWidth + gap) + imagePadding * 2 + gapBetweenLabelsAndGrid + maxHorizontalLabelWidth

    private fun getImageWidth(maxVerticalLabelsWidth: Int): Int =
            imagePadding * 2 + maxVerticalLabelsWidth + gapBetweenLabelsAndGrid + grid.first().size * (cellWidth + gap) - gap

    private fun drawVerticalLabels(graphics: Graphics2D, verticalLabels: List<String>, maxStringWidth: Int) {
        graphics.color = Color.black
        verticalLabels.forEachIndexed { i, label ->
            graphics.drawString(label,
                    imagePadding + (maxStringWidth - graphics.fontMetrics.stringWidth(label)),
                    imagePadding + i * (cellWidth + gap) + 15)
        }
    }

    private fun drawHorizontalLabels(graphics: Graphics2D, horizontalLabels: List<String>, maxHorizontalLabelWidth: Int) {
        // todo: implement
    }

    private fun getMaxStringWidth(labels: List<String>): Int {
        /* BufferedImage is created only to get graphics. */
        val graphics = BufferedImage(42, 42, BufferedImage.TYPE_INT_ARGB).createGraphics()
        graphics.font = font
        var maxWidth = 0
        for (label in labels) {
            val width = graphics.fontMetrics.stringWidth(label)
            if (width > maxWidth) {
                maxWidth = width
            }
        }
        return maxWidth
    }

    private fun draw(graphics: Graphics, maxVerticalLabelWidth: Int) {
        grid.forEachIndexed { i, row ->
            row.forEachIndexed { j, cell ->
                graphics.color = if (cell) green else gray
                graphics.fillRect(imagePadding + maxVerticalLabelWidth + gapBetweenLabelsAndGrid + j * (cellWidth + gap),
                        imagePadding + i * (cellWidth + gap),
                        cellWidth, cellWidth)
            }
        }
    }
}