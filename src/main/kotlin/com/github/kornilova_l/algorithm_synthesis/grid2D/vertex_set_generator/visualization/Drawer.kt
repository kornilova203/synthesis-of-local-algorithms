package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val blue = Color(51, 99, 255)
val gray = Color(230, 230, 230)
val white = Color(255, 255, 255)
val lightGreen = Color(0, 255, 136)
val cellWidth = 15
val gap = 7

fun drawLabels(labels: Array<BooleanArray>, independentSet: Array<BooleanArray>) {
    val imageHeight = labels.size * (cellWidth + gap) + gap
    val imageWidth = labels[0].size * (cellWidth + gap) + gap
    val bufferedImage = BufferedImage(imageWidth, imageHeight,
            BufferedImage.TYPE_INT_ARGB)
    val graphics = createGraphics(bufferedImage, imageWidth, imageHeight)
    drawSquares(graphics, labels, independentSet)
    val file = File("generated_images/grid.png")
    ImageIO.write(bufferedImage, "png", file)
}

private fun drawSquares(graphics: Graphics, labels: Array<BooleanArray>, independentSet: Array<BooleanArray>) {
    for (i in 0 until labels.size) {
        for (j in 0 until labels[0].size) {
            if (labels[i][j]) {
                graphics.color = blue
            } else {
                graphics.color = gray
            }
            graphics.fillRect(gap + j * (cellWidth + gap), gap + i * (cellWidth + gap), cellWidth, cellWidth)
            if (independentSet[i][j]) {
                graphics.color = lightGreen
                graphics.drawRect(gap + j * (cellWidth + gap), gap + i * (cellWidth + gap), cellWidth, cellWidth)
            }
        }
    }
}

private fun createGraphics(bufferedImage: BufferedImage, imageWidth: Int, imageHeight: Int): Graphics {
    val graphics = bufferedImage.createGraphics()
    graphics.color = white
    graphics.fillRect(0, 0, imageWidth, imageHeight)
    graphics.stroke = BasicStroke(2f)
    return graphics
}
