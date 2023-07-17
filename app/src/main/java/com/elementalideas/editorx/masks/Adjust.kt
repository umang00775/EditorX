package com.elementalideas.editorx.masks

import android.app.Activity
import android.graphics.*
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicConvolve3x3
import android.renderscript.ScriptIntrinsicBlur




class Adjust {

    // Adjust brightness
    fun adjustBrightness(bitmap: Bitmap, brightness: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val brightnessMatrix = floatArrayOf(
            1f, 0f, 0f, 0f, brightness.toFloat(),
            0f, 1f, 0f, 0f, brightness.toFloat(),
            0f, 0f, 1f, 0f, brightness.toFloat(),
            0f, 0f, 0f, 1f, 0f
        )

        val brightnessFilter = ColorMatrixColorFilter(brightnessMatrix)

        val brightnessBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(brightnessBitmap)
        val paint = Paint()

        paint.colorFilter = brightnessFilter
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return brightnessBitmap
    }

    // Adjust contrast
    fun adjustContrast(bitmap: Bitmap, contrast: Int): Bitmap {
        val contrastValue = (contrast + 100) / 100f
        val matrix = floatArrayOf(
            contrastValue, 0f, 0f, 0f, 128 * (1 - contrastValue),
            0f, contrastValue, 0f, 0f, 128 * (1 - contrastValue),
            0f, 0f, contrastValue, 0f, 128 * (1 - contrastValue),
            0f, 0f, 0f, 1f, 0f
        )

        val colorMatrix = ColorMatrix(matrix)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(colorMatrix)
        }

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }

    // Adjust saturation
    fun adjustSaturation(bitmap: Bitmap, saturation: Int): Bitmap {
        val hsvMatrix = FloatArray(3)
        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)

        for (x in 0 until bitmap.width) {
            for (y in 0 until bitmap.height) {
                val pixel = bitmap.getPixel(x, y)
                Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsvMatrix)
                hsvMatrix[1] += saturation / 100.0f
                if (hsvMatrix[1] < 0.0f) {
                    hsvMatrix[1] = 0.0f
                } else if (hsvMatrix[1] > 1.0f) {
                    hsvMatrix[1] = 1.0f
                }
                val adjustedPixel = Color.HSVToColor(Color.alpha(pixel), hsvMatrix)
                adjustedBitmap.setPixel(x, y, adjustedPixel)
            }
        }

        return adjustedBitmap
    }

    // Adjust exposure
    fun adjustExposure(bitmap: Bitmap, exposure: Int): Bitmap {
        val matrix = ColorMatrix()
        matrix.set(floatArrayOf(
            1f, 0f, 0f, 0f, exposure / 100f,
            0f, 1f, 0f, 0f, exposure / 100f,
            0f, 0f, 1f, 0f, exposure / 100f,
            0f, 0f, 0f, 1f, 0f
        ))

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }

    // Adjust shadows
    fun adjustShadows(bitmap: Bitmap, shadows: Int): Bitmap {
        val matrix = ColorMatrix()
        val shadowsValue = shadows / 100f
        matrix.set(floatArrayOf(
            1f + shadowsValue, 0f, 0f, 0f, 0f,
            0f, 1f + shadowsValue, 0f, 0f, 0f,
            0f, 0f, 1f + shadowsValue, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }

    // Color temperature
    fun adjustColorTemperature(bitmap: Bitmap, colorTemperature: Int): Bitmap {
        val matrix = ColorMatrix()

        val redMultiplier = calculateRedMultiplierTempAdjust(colorTemperature)
        val blueMultiplier = calculateBlueMultiplierTempAdjust(colorTemperature)

        matrix.set(floatArrayOf(
            redMultiplier, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, blueMultiplier, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ))

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }
    private fun calculateRedMultiplierTempAdjust(colorTemperature: Int): Float {
        val normalizedValue = colorTemperature / 100f
        return if (normalizedValue > 0) {
            1f - normalizedValue * 0.05f
        } else {
            1f + normalizedValue * 0.1f
        }
    }
    private fun calculateBlueMultiplierTempAdjust(colorTemperature: Int): Float {
        val normalizedValue = colorTemperature / 100f
        return if (normalizedValue > 0) {
            1f + normalizedValue * 0.1f
        } else {
            1f - normalizedValue * 0.05f
        }
    }

    // Tone adjust
    fun adjustTone(bitmap: Bitmap, tone: Int): Bitmap {
        val matrix = ColorMatrix()

        val toneMultiplier = calculateToneMultiplierToneAdjust(tone)

        matrix.setSaturation(toneMultiplier)

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(matrix)
        }

        val canvas = Canvas(adjustedBitmap)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }
    private fun calculateToneMultiplierToneAdjust(tone: Int): Float {
        val normalizedValue = tone / 100f
        return if (normalizedValue > 0) {
            1f - normalizedValue * 0.1f
        } else {
            1f + normalizedValue * 0.1f
        }
    }


    // Adjust Vignette
    fun adjustVignette(bitmap: Bitmap, vignette: Int): Bitmap {
        val outerRadiusRatio = calculateOuterRadiusRatioVignetteAdjust(vignette)

        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(adjustedBitmap)

        val gradient = RadialGradient(
            bitmap.width / 2f, bitmap.height / 2f, bitmap.width * outerRadiusRatio,
            intArrayOf(Color.TRANSPARENT, Color.BLACK),
            floatArrayOf(0f, 1f),
            Shader.TileMode.CLAMP
        )

        val paint = Paint().apply {
            isAntiAlias = true
            shader = ComposeShader(BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP), gradient, PorterDuff.Mode.SRC_OVER)
        }

        canvas.drawPaint(paint)

        return adjustedBitmap
    }
    private fun calculateOuterRadiusRatioVignetteAdjust(vignette: Int): Float {
        val normalizedValue = vignette / 100f
        return 1f - normalizedValue * 0.5f
    }

    // Adjust brilliance
    fun adjustBrilliance(bitmap: Bitmap, brilliance: Int): Bitmap {
        val adjustedBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        val canvas = Canvas(adjustedBitmap)

        val paint = Paint().apply {
            colorFilter = ColorMatrixColorFilter(createBrillianceMatrix(brilliance))
        }

        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return adjustedBitmap
    }
    private fun createBrillianceMatrix(brilliance: Int): ColorMatrix {
        val normalizedValue = brilliance / 100f
        val matrix = ColorMatrix()
        matrix.setScale(normalizedValue, normalizedValue, normalizedValue, 1f)
        return matrix
    }

    // Adjust sharpness
    fun adjustSharpness(bitmap: Bitmap, sharpness: Int, context: Activity): Bitmap {
        val adjustedBitmap = bitmap.copy(bitmap.config, true)
        val radius = mapValueSharpnessAdjust(sharpness, 0f, 100f, 0.1f, 25f)

        val rs = RenderScript.create(context)
        val input = Allocation.createFromBitmap(rs, adjustedBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT)
        val output = Allocation.createTyped(rs, input.type)

        val script = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs))
        val coefficients = floatArrayOf(
            -radius, -radius, -radius,
            -radius, 1 + (8 * radius), -radius,
            -radius, -radius, -radius
        )

        script.setCoefficients(coefficients)
        script.setInput(input)
        script.forEach(output)

        output.copyTo(adjustedBitmap)

        input.destroy()
        output.destroy()
        script.destroy()
        rs.destroy()

        return adjustedBitmap
    }
    private fun mapValueSharpnessAdjust(value: Int, fromMin: Float, fromMax: Float, toMin: Float, toMax: Float): Float {
        return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin
    }

    // Adjust blur
    fun adjustBlur(bitmap: Bitmap, blurriness: Int, context: Activity): Bitmap {
        var blurValue = blurriness
        if(blurriness == 0) blurValue = 1

        val renderScript = RenderScript.create(context)
        val inputAllocation = Allocation.createFromBitmap(renderScript, bitmap)
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val outputAllocation = Allocation.createTyped(renderScript, inputAllocation.type)
        val blurRadius = mapBlurValueToRadius(blurValue)

        val script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
        script.setInput(inputAllocation)
        script.setRadius(blurRadius)
        script.forEach(outputAllocation)
        outputAllocation.copyTo(outputBitmap)

        inputAllocation.destroy()
        outputAllocation.destroy()
        script.destroy()
        renderScript.destroy()

        return outputBitmap

    }
    private fun mapBlurValueToRadius(blurValue: Int): Float {
        val clampedValue = blurValue.coerceIn(0, 100)
        val maxRadius = 25f // Adjust this value based on desired maximum blur radius
        return (maxRadius * clampedValue) / 100f
    }

}



