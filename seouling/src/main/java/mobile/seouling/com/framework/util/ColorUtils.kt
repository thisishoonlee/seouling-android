package mobile.seouling.com.framework.util

import android.graphics.Color
import androidx.annotation.ColorInt

object ColorUtils {
    @JvmStatic
    fun render(color1: Int, color2: Int): Int {
        if (Color.alpha(color1) != 0xff) {
            throw IllegalArgumentException("color1 should be opaque!")
        }
        val progress = Color.alpha(color2).toFloat() / 0xff
        return mix(color1, color2, progress)
    }

    @JvmStatic
    fun argb(alpha: Int, rgb: Int): Int {
        return alpha shl 24 or (rgb and 0x00ffffff)
    }

    private fun mix(color1: Int, color2: Int, progress: Float): Int {
        return mix(color1, color2, 0xff, progress)
    }

    private fun mix(color1: Int, color2: Int, alpha: Int, progress: Float): Int {
        val r1 = Color.red(color1)
        val g1 = Color.green(color1)
        val b1 = Color.blue(color1)
        val r2 = Color.red(color2)
        val g2 = Color.green(color2)
        val b2 = Color.blue(color2)
        val factor1 = 1f - progress
        val r3 = (r1 * factor1 + r2 * progress).toInt()
        val g3 = (g1 * factor1 + g2 * progress).toInt()
        val b3 = (b1 * factor1 + b2 * progress).toInt()
        return Color.argb(alpha, r3, g3, b3)
    }

    private fun translateAlpha(color1: Int, color2: Int, progress: Float): Int {
        val factor1 = 1f - progress
        return (Color.alpha(color1) * factor1 + Color.alpha(color2) * progress).toInt()
    }

    @JvmStatic
    fun translate(color1: Int, color2: Int, progress: Float): Int {
        return mix(
            color1,
            color2,
            translateAlpha(color1, color2, progress),
            progress
        )
    }

    @JvmStatic
    fun renderAlpha(color: Int, alpha: Float): Int {
        return argb((Color.alpha(color) * alpha).toInt(), color)
    }

    @ColorInt
    fun argb(color: String): Int {
        checkColorString(color)
        return Color.parseColor(color)
    }

    @JvmStatic
    @ColorInt
    fun rgba(color: String): Int {
        try {
            checkColorString(color)
        } catch (e: Exception) {
            return Color.TRANSPARENT
        }
        return if (color.length == 9) {
            val alpha = color.substring(7, 9).toInt(16)
            val c = Color.parseColor(color.substring(0, 7))
            argb(alpha, c)
        } else {
            Color.parseColor(color)
        }
    }

    private fun checkColorString(color: String) {
        if (color.isEmpty() || color[0] != '#') {
            throw IllegalArgumentException("Invalid color format $color")
        }
    }
}
