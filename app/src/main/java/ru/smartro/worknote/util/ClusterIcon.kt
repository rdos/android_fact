package ru.smartro.worknote.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.yandex.runtime.image.ImageProvider
import kotlin.math.abs
import kotlin.math.sqrt

class ClusterIcon(private val text: String, private val context: AppCompatActivity) : ImageProvider() {
    private val FONT_SIZE = 15f
    private val MARGIN_SIZE = 3f
    private val STROKE_SIZE = 3f

    override fun getId(): String {
        return "text_$text"
    }
    override fun getImage(): Bitmap {
        val metrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(metrics)
        } else {
            @Suppress("DEPRECATION")
            val display = context.windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(metrics)
        }

        val textPaint = Paint()
        textPaint.textSize = FONT_SIZE * metrics.density
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.style = Paint.Style.FILL
        textPaint.isAntiAlias = true
        val widthF = textPaint.measureText(text)
        val textMetrics = textPaint.fontMetrics
        val heightF = abs(textMetrics.bottom) + abs(textMetrics.top)
        val textRadius = sqrt(widthF * widthF + heightF * heightF.toDouble()).toFloat() / 2
        val internalRadius: Float = textRadius + MARGIN_SIZE * metrics.density
        val externalRadius: Float =
            internalRadius + STROKE_SIZE * metrics.density
        val width = (2 * externalRadius + 0.5).toInt()
        val bitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val backgroundPaint = Paint()
        backgroundPaint.isAntiAlias = true
        backgroundPaint.color = Color.BLUE
        canvas.drawCircle(width / 2.toFloat(), width / 2.toFloat(), externalRadius, backgroundPaint)
        backgroundPaint.color = Color.WHITE
        canvas.drawCircle(width / 2.toFloat(), width / 2.toFloat(), internalRadius, backgroundPaint)
        canvas.drawText(text, width / 2.toFloat(), width / 2 - (textMetrics.ascent + textMetrics.descent) / 2, textPaint)
        return bitmap
    }
}