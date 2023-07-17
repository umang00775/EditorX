package com.elementalideas.editorx.masks

import android.graphics.Bitmap
import android.graphics.Matrix

class Rotate {
    fun rotateBitmap(bitmap: Bitmap, degrees: Int): Bitmap {
        val rotationAngle = degrees.toFloat()
        val matrix = Matrix()
        matrix.postRotate(rotationAngle)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}