package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val blue = Color(110, 145, 255)
val gray = Color(224, 224, 224)
val darkBlue = Color(2, 15, 56)
const val cellWidth = 19
const val independentSetWidth = 7
private const val gap = 7

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
                graphics.fillRect(gap + j * (cellWidth + gap), gap + i * (cellWidth + gap), cellWidth, cellWidth)
            } else {
                graphics.color = gray
                graphics.drawRect(gap + j * (cellWidth + gap), gap + i * (cellWidth + gap), cellWidth, cellWidth)
            }
            if (independentSet[i][j]) {
                graphics.color = darkBlue
                graphics.fillRect(gap + j * (cellWidth + gap) + (cellWidth - independentSetWidth) / 2,
                        gap + i * (cellWidth + gap) + (cellWidth - independentSetWidth) / 2,
                        independentSetWidth, independentSetWidth)
            }
        }
    }
}

internal fun createGraphics(bufferedImage: BufferedImage, imageWidth: Int, imageHeight: Int): Graphics {
    val graphics = bufferedImage.createGraphics()
    graphics.color = Color.WHITE
    graphics.fillRect(0, 0, imageWidth, imageHeight)
    graphics.stroke = BasicStroke(2f)
    return graphics
}
