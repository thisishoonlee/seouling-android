package mobile.seouling.com.base_android

import android.content.Context
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun Context.toPixels(unit: Int = TypedValue.COMPLEX_UNIT_DIP, value: Number): Int =
    TypedValue.applyDimension(unit, value.toFloat(), resources.displayMetrics).toInt()

fun Context.getColorCompat(@ColorRes colorId: Int) = ContextCompat.getColor(this, colorId)