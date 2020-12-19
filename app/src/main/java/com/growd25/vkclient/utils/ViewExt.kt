package com.growd25.vkclient.utils

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import kotlin.math.roundToInt

val Float.pixelToDp
    get() = Resources.getSystem().displayMetrics.density * this

val Int.dpToPixel
    get() = (Resources.getSystem().displayMetrics.density * this).roundToInt()

val Float.spToPixel
    get() = Resources.getSystem().displayMetrics.scaledDensity * this

val Int.spToPixel
    get() = (Resources.getSystem().displayMetrics.scaledDensity * this).roundToInt()

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun View.visibility(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}
