package com.example.skinfriend.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView

class ParallaxScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {

    var parallaxView: View? = null
    private var parallaxFactor = 0.5f // Faktor untuk memperlambat scroll (default 50%)

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        // Jika ada view yang ingin diberi efek parallax
        parallaxView?.translationY = t * parallaxFactor
    }

    // Metode untuk mengatur view dengan efek parallax
    fun setParallaxView(view: View, factor: Float = 0.5f) {
        parallaxView = view
        parallaxFactor = factor
    }
}