package com.elementalideas.editorx.masks

import android.graphics.Color

class Static {
    companion object{
        val sharp = floatArrayOf(
            -1f, -1f, -1f,
            -1f, 9f, -1f,
            -1f, -1f, -1f
        )

        const val  RED_HEX_CODE = "#FF0000"
        const val  PINK_HEX_CODE = "#FFC0CB"
        const val  ORANGE_HEX_CODE = "#FFA500"
        const val  YELLOW_HEX_CODE = "#FFFF00"
        const val  GREEN_HEX_CODE = "#00FF00"
        const val  BLUE_HEX_CODE = "#0000FF"
        const val  PURPLE_HEX_CODE = "#800080"
        const val  HOT_PINK_HEX_CODE = "#FF69B4"
        const val  CYAN_HEX_CODE = "#00FFFF"
        const val  ORANGE_RED_HEX_CODE = "#FF4500"

        val COLOR_LIST_COLORIZATION = listOf(
            Color.RED,
            Color.GREEN,
            Color.BLUE,
            Color.YELLOW,
            Color.MAGENTA,
            Color.CYAN,
            Color.rgb(255, 165, 0),
            Color.rgb(138, 43, 226),
            Color.rgb(0, 255, 255),
            Color.rgb(255, 140, 0)
        )

    }
}