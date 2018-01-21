package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val font = Font("Arial", 0, 14)
private const val verticalGap = 1
private const val horizontalGap = 6

fun main(args: Array<String>) {
    val points = listOf(
            Point("0", true),
            Point("0, 1", false),
            Point("1, 2, 3, 4", true),
            Point("1, 2, 3", false),
            Point("2, 3", false),
            Point("1, 3", false),
            Point("1, 2, 3", true)
    )
    draw(points)
}

fun draw(points: List<Point>) {
    val maxStringWidth = getMaxStringWidth(points)
    val imageHeight = points.size * (cellWidth + verticalGap) + verticalGap
    val imageWidth = maxStringWidth + cellWidth + horizontalGap * 4
    val bufferedImage = BufferedImage(imageWidth, imageHeight,
            BufferedImage.TYPE_INT_ARGB)
    val graphics = createGraphics(bufferedImage, imageWidth, imageHeight)
    graphics.font = font
    draw(graphics, points, maxStringWidth)
    val file = File("generated_images/scheme.png")
    ImageIO.write(bufferedImage, "png", file)
}

fun getMaxStringWidth(points: List<Point>): Int {
    val graphics = BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics()
    graphics.font = font
    var maxWidth = 0
    for (point in points) {
        val width = graphics.fontMetrics.stringWidth(point.label)
        if (width > maxWidth) {
            maxWidth = width
        }
    }
    return maxWidth
}

fun draw(graphics: Graphics, points: List<Point>, maxStringWidth: Int) {
    points.forEachIndexed { i, point ->
        if (point.isOn) {
            graphics.color = blue
        } else {
            graphics.color = gray
        }
        graphics.fillRect(maxStringWidth + horizontalGap * 2, verticalGap + i * (cellWidth + verticalGap), cellWidth, cellWidth)
        graphics.color = Color.black
        graphics.drawString(point.label,
                horizontalGap + (maxStringWidth - graphics.fontMetrics.stringWidth(point.label)),
                verticalGap + i * (cellWidth + verticalGap) + 15)
    }
}

data class Point(val label: String, val isOn: Boolean)