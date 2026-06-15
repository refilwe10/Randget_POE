package com.example.randget11

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Required Feature: Displays in a visual format how well the user is
 * doing with staying between their minimum and maximum spending goals
 * over the past month.
 *
 * Shows a cumulative spending line over the days of the month, with
 * horizontal limit lines for the min and max budget goals, plus a
 * progress bar summarising the overall position relative to the goals.
 */
class GoalProgressActivity : AppCompatActivity() {

    private val TAG = "GoalProgressActivity"

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.goal_progress)

        db = DatabaseProvider.getDatabase(this)

        val monthInput = findViewById<EditText>(R.id.etMonth)
        val btnLoad = findViewById<Button>(R.id.btnLoad)
        val lineChart = findViewById<LineChart>(R.id.lineChart)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvSummary = findViewById<TextView>(R.id.tvSummary)
        val tvProgressLabel = findViewById<TextView>(R.id.tvProgressLabel)

        // Default to the current month for convenience
        val sdf = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        monthInput.setText(sdf.format(Date()))

        btnLoad.setOnClickListener {
            val month = monthInput.text.toString().trim()

            if (month.isBlank()) {
                Toast.makeText(this, "Enter a month", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d(TAG, "Loading goal progress for $month")

            CoroutineScope(Dispatchers.IO).launch {
                val startDate = "$month-01"
                val endDate = "$month-31"

                val dailyTotals = db.expenseDao().getDailyTotalsBetweenDates(startDate, endDate)
                val totalSpent = db.expenseDao().getTotalForMonth(month) ?: 0.0
                val budget = db.budgetDao().getBudgetForMonth(month)

                withContext(Dispatchers.Main) {
                    drawProgressChart(lineChart, dailyTotals, budget)
                    updateSummary(tvSummary, tvProgressLabel, progressBar, totalSpent, budget)
                }
            }
        }
    }

    private fun drawProgressChart(chart: LineChart, dailyTotals: List<CategoryTotal>, budget: Budget?) {
        if (dailyTotals.isEmpty()) {
            chart.clear()
            chart.invalidate()
            return
        }

        // Build a cumulative spending line - day index vs running total
        var running = 0.0
        val entries = dailyTotals.mapIndexed { index, item ->
            running += item.totalAmount
            Entry(index.toFloat(), running.toFloat())
        }

        val dataSet = LineDataSet(entries, "Cumulative Spending")
        dataSet.color = Color.parseColor("#FF9800")
        dataSet.setCircleColor(Color.parseColor("#FF9800"))
        dataSet.lineWidth = 2.5f
        dataSet.valueTextSize = 0f

        chart.data = LineData(dataSet)

        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
        }

        chart.axisLeft.removeAllLimitLines()
        if (budget != null) {
            val maxLine = LimitLine(budget.maxAmount.toFloat(), "Max Goal")
            maxLine.lineColor = Color.RED
            maxLine.lineWidth = 2f
            maxLine.textSize = 12f

            val minLine = LimitLine(budget.minAmount.toFloat(), "Min Goal")
            minLine.lineColor = Color.parseColor("#388E3C")
            minLine.lineWidth = 2f
            minLine.textSize = 12f

            chart.axisLeft.addLimitLine(maxLine)
            chart.axisLeft.addLimitLine(minLine)
        }

        chart.description.isEnabled = false
        chart.animateX(600)
        chart.invalidate()
    }

    private fun updateSummary(
        tvSummary: TextView,
        tvProgressLabel: TextView,
        progressBar: ProgressBar,
        totalSpent: Double,
        budget: Budget?
    ) {
        if (budget == null) {
            tvSummary.text = "Total Spent: R %.2f. No budget goals set for this month.".format(totalSpent)
            progressBar.progress = 0
            tvProgressLabel.text = ""
            return
        }

        tvSummary.text = "Total Spent: R %.2f  |  Goal range: R %.2f - R %.2f".format(
            totalSpent, budget.minAmount, budget.maxAmount
        )

        // Progress is expressed as a % of the way from 0 towards the max goal
        val percent = if (budget.maxAmount > 0) {
            ((totalSpent / budget.maxAmount) * 100).toInt().coerceIn(0, 100)
        } else 0

        progressBar.progress = percent

        tvProgressLabel.text = when {
            totalSpent < budget.minAmount ->
                "You're below your minimum goal (%d%% of max). Keep tracking - there's room to spend if needed.".format(percent)
            totalSpent <= budget.maxAmount ->
                "Nice! You're within your goal range (%d%% of max budget used).".format(percent)
            else ->
                "You've exceeded your maximum goal (%d%% of max budget used)!".format(percent)
        }

        Log.d(TAG, "Goal progress: $percent% of max budget used")
    }
}
