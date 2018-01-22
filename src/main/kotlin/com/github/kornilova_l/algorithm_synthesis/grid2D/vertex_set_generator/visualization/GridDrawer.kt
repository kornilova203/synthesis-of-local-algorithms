package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
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
        drawHorizontalLabels(graphics, horizontalLabels, maxVerticalLabelWidth)
        draw(graphics, maxVerticalLabelWidth)
        ImageIO.write(bufferedImage, "png", file)
    }

    protected open fun getImageHeight(maxHorizontalLabelWidth: Int): Int =
            grid.size * (cellWidth + gap) + imagePadding * 2 + gapBetweenLabelsAndGrid + (maxHorizontalLabelWidth * 0.7).toInt()

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

    private fun drawHorizontalLabels(graphics: Graphics2D, horizontalLabels: List<String>, maxVerticalLabelWidth: Int) {
        val affineTransform = AffineTransform()
        affineTransform.rotate(Math.toRadians(45.0), 0.0, 0.0)
        val rotatedFont = font.deriveFont(affineTransform)
        graphics.font = rotatedFont
        horizontalLabels.forEachIndexed { j, label ->
            graphics.drawString(label,
                    imagePadding + maxVerticalLabelWidth + gapBetweenLabelsAndGrid + j * (cellWidth + gap),
                    imagePadding + grid.size * (cellWidth + gap) - gap + gapBetweenLabelsAndGrid + 7)
        }
        graphics.font = font
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

    companion object {
        /**
         * Format if file:
         * <number of rows> <number of columns>
         * <names of rows. One name per line.>
         * <names of columns. One name per line.>
         * 01010...
         * 10101...
         * ...
         */
        fun createInstance(file: File): GridDrawer {
            BufferedReader(FileReader(file)).use { reader ->
                val firstLine = reader.readLine()
                val parts = firstLine.split(" ")
                val n = Integer.parseInt(parts[0])
                val m = Integer.parseInt(parts[1])
                val verticalLabels = ArrayList<String>(n)
                for (i in 0 until n) {
                    verticalLabels.add(reader.readLine())
                }
                val horizontalLabels = ArrayList<String>(n)
                for (i in 0 until m) {
                    horizontalLabels.add(reader.readLine())
                }
                val grid = Array(n, { BooleanArray(m) })
                for (i in 0 until n) {
                    for (j in 0 until m) {
                        var c = reader.read().toChar()
                        while (c != '1' && c != '0') {
                            c = reader.read().toChar()
                        }
                        grid[i][j] = c == '1'
                    }
                }
                return GridDrawer(verticalLabels, horizontalLabels, grid)
            }
        }
    }
}