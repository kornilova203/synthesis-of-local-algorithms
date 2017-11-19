package com.github.kornilova_l.algorithm_synthesis.grid2D.vertex_set_generator.visualization

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

val blue = Color(51, 99, 255)
val gray = Color(190, 190, 190)
val white = Color(255, 255, 255)
val cellWidth = 15
val gap = 7

fun drawLabels(labels: Array<BooleanArray>) {
    val imageHeight = labels.size * (cellWidth + gap) + gap
    val imageWidth = labels[0].size * (cellWidth + gap) + gap
    val bufferedImage = BufferedImage(imageWidth, imageHeight,
            BufferedImage.TYPE_INT_ARGB)
    val graphics = bufferedImage.createGraphics()
    graphics.color = white
    graphics.fillRect(0, 0, imageWidth, imageHeight)
    for (i in 0 until labels.size) {
        for (j in 0 until labels[0].size) {
            if (labels[i][j]) {
                graphics.color = blue
            } else {
                graphics.color = gray
            }
            graphics.fillRect(gap + j * (cellWidth + gap), gap + i * (cellWidth + gap), cellWidth, cellWidth)
        }
    }
    val file = File("generated_images/grid.png")
    ImageIO.write(bufferedImage, "png", file)

}