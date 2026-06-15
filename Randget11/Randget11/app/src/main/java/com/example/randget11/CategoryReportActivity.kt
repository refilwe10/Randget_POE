package com.example.randget11

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import kotlinx.coroutines.*

class CategoryReportActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_report)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budget_db"
        ).build()

        val start = findViewById<EditText>(R.id.etStartDate)
        val end = findViewById<EditText>(R.id.etEndDate)
        val btn = findViewById<Button>(R.id.btnGenerate)
        val recycler = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerReport)
        val barChart = findViewById<SimpleBarChartView>(R.id.barChart)

        recycler.layoutManager = LinearLayoutManager(this)

        btn.setOnClickListener {
            val s = start.text.toString()
            val e = end.text.toString()

            if (s.isEmpty() || e.isEmpty()) {
                Toast.makeText(this, "Enter dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val results = db.expenseDao().getTotalSpentPerCategory(s, e)

                // Use the month of the start date (YYYY-MM) to look up the
                // budget goals to display alongside the graph.
                val month = if (s.length >= 7) s.substring(0, 7) else s
                val budget = db.budgetDao().getBudgetForMonth(month)

                withContext(Dispatchers.Main) {
                    recycler.adapter = CategoryReportAdapter(results)
                    barChart.setData(
                        categoryTotals = results,
                        minGoal = budget?.minAmount,
                        maxGoal = budget?.maxAmount
                    )
                }
            }
        }
    }
}