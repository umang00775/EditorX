package com.elementalideas.editorx.masks

import android.graphics.*
import java.util.*

class Filters {
    // Colorize
    fun colorizeImage(bitmap: Bitmap, hexColor: String): Bitmap {
        val color = Color.parseColor(hexColor)

        val editedBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        val alpha = Color.alpha(color)

        val matrix = floatArrayOf(
            red / 255f, 0f, 0f, 0f, 0f,
            0f, green / 255f, 0f, 0f, 0f,
            0f, 0f, blue / 255f, 0f, 0f,
            0f, 0f, 0f, alpha / 255f, 0f
        )

        val colorMatrix = ColorMatrix(matrix)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        val canvas = Canvas(editedBitmap)
        canvas.drawBitmap(editedBitmap, 0f, 0f, paint)

        return editedBitmap
    }

    //Oiled image effect
    fun applyOilEffect(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val oilBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val radius = 8
        val levels = 256

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val resultPixels = IntArray(width * height)

        val random = Random()

        for (x in 0 until width) {
            for (y in 0 until height) {
                val centerPixel = pixels[y * width + x]

                val r = Color.red(centerPixel)
                val g = Color.green(centerPixel)
                val b = Color.blue(centerPixel)

                val intensity = (r + g + b) / 3

                val offset = random.nextInt(levels) - levels / 2

                val newR = clampValue(r + offset, 0, 255)
                val newG = clampValue(g + offset, 0, 255)
                val newB = clampValue(b + offset, 0, 255)

                val newPixel = Color.rgb(newR, newG, newB)

                for (dx in -radius..radius) {
                    for (dy in -radius..radius) {
                        if (x + dx in 0 until width && y + dy in 0 until height) {
                            val currentPixel = pixels[(y + dy) * width + (x + dx)]

                            val currentR = Color.red(currentPixel)
                            val currentG = Color.green(currentPixel)
                            val currentB = Color.blue(currentPixel)

                            val currentIntensity = (currentR + currentG + currentB) / 3

                            if (Math.abs(intensity - currentIntensity) < Math.abs(intensity - Color.red(resultPixels[y * width + x]))) {
                                resultPixels[y * width + x] = newPixel
                            }
                        }
                    }
                }
            }
        }

        oilBitmap.setPixels(resultPixels, 0, width, 0, 0, width, height)

        return oilBitmap
    }
    private fun clampValue(value: Int, min: Int, max: Int): Int {
        return when {
            value < min -> min
            value > max -> max
            else -> value
        }
    }

    // Black and white
    fun convertToBlackAndWhite(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val blackAndWhiteBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = bitmap.getPixel(x, y)
                val red = Color.red(pixel)
                val green = Color.green(pixel)
                val blue = Color.blue(pixel)

                val grayscale = (red * 0.299 + green * 0.587 + blue * 0.114).toInt()
                val newPixel = Color.rgb(grayscale, grayscale, grayscale)

                blackAndWhiteBitmap.setPixel(x, y, newPixel)
            }
        }

        return blackAndWhiteBitmap
    }

    // Random colorization
    fun randomColorization(bitmap: Bitmap, colors: List<Int>): Bitmap {
        val coloredBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val random = Random()

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                val color = colors[random.nextInt(colors.size)]

                val red = Color.red(color)
                val green = Color.green(color)
                val blue = Color.blue(color)

                val coloredPixel = Color.rgb(gray * red / 255, gray * green / 255, gray * blue / 255)
                coloredBitmap.setPixel(x, y, coloredPixel)
            }
        }

        return coloredBitmap
    }
}