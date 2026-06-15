package com.example.randget11

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * A simple, lightweight bar chart that draws the total amount
 * spent per category as vertical bars, with two horizontal
 * reference lines showing the minimum and maximum budget goals.
 *
 * Kept intentionally simple (plain Canvas drawing) so no extra
 * charting libraries are required.
 */
class SimpleBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var categories: List<CategoryTotal> = emptyList()
    private var minGoal: Double? = null
    private var maxGoal: Double? = null

    private val barPaint = Paint().apply {
        color = Color.parseColor("#3F51B5")
        style = Paint.Style.FILL
    }

    private val minLinePaint = Paint().apply {
        color = Color.parseColor("#4CAF50")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val maxLinePaint = Paint().apply {
        color = Color.parseColor("#F44336")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private val axisTextPaint = Paint().apply {
        color = Color.DKGRAY
        textSize = 24f
        textAlign = Paint.Align.LEFT
    }

    /**
     * Updates the chart with new data and redraws it.
     *
     * @param categoryTotals the amount spent per category for the selected period
     * @param minGoal the minimum spending goal for the period (nullable if no budget set)
     * @param maxGoal the maximum spending goal for the period (nullable if no budget set)
     */
    fun setData(categoryTotals: List<CategoryTotal>, minGoal: Double?, maxGoal: Double?) {
        this.categories = categoryTotals
        this.minGoal = minGoal
        this.maxGoal = maxGoal
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        if (categories.isEmpty()) {
            canvas.drawText("No data for selected period", width / 2f, height / 2f, textPaint)
            return
        }

        val paddingLeft = 60f
        val paddingBottom = 60f
        val paddingTop = 40f
        val chartWidth = width - paddingLeft - 20f
        val chartHeight = height - paddingBottom - paddingTop

        // Determine the max value to scale the chart, including the goals so
        // the reference lines are always visible.
        val maxBarValue = categories.maxOf { it.totalAmount }
        val maxValue = maxOf(maxBarValue, maxGoal ?: 0.0, minGoal ?: 0.0, 1.0)

        val barCount = categories.size
        val slotWidth = chartWidth / barCount
        val barWidth = slotWidth * 0.6f

        categories.forEachIndexed { index, category ->
            val barHeight = (category.totalAmount / maxValue).toFloat() * chartHeight
            val left = paddingLeft + index * slotWidth + (slotWidth - barWidth) / 2f
            val top = paddingTop + chartHeight - barHeight
            val right = left + barWidth
            val bottom = paddingTop + chartHeight

            canvas.drawRect(left, top, right, bottom, barPaint)

            // Category name below the bar
            canvas.drawText(
                category.categoryName,
                left + barWidth / 2f,
                height - 10f,
                axisTextPaint
            )

            // Amount above the bar
            canvas.drawText(
                "R%.0f".format(category.totalAmount),
                left + barWidth / 2f,
                top - 8f,
                textPaint
            )
        }

        // Draw min goal line (green)
        minGoal?.let { min ->
            val y = paddingTop + chartHeight - (min / maxValue).toFloat() * chartHeight
            canvas.drawLine(paddingLeft, y, width - 10f, y, minLinePaint)
            canvas.drawText("Min Goal", paddingLeft + 60f, y - 6f, axisTextPaint)
        }

        // Draw max goal line (red)
        maxGoal?.let { max ->
            val y = paddingTop + chartHeight - (max / maxValue).toFloat() * chartHeight
            canvas.drawLine(paddingLeft, y, width - 10f, y, maxLinePaint)
            canvas.drawText("Max Goal", paddingLeft + 60f, y - 6f, axisTextPaint)
        }
    }
}