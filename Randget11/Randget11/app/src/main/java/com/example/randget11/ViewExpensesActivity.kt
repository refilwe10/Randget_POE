package com.example.randget11

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.*

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_expenses)
        db = DatabaseProvider.getDatabase(this)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budget_db"
        ).build()

        val startDate = findViewById<EditText>(R.id.etStartDate)
        val endDate = findViewById<EditText>(R.id.etEndDate)
        val btnLoad = findViewById<Button>(R.id.btnLoad)
        recyclerView = findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        btnLoad.setOnClickListener {

            val start = startDate.text.toString()
            val end = endDate.text.toString()

            if (start.isEmpty() || end.isEmpty()) {
                Toast.makeText(this, "Enter both dates", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {

                val expenses = db.expenseDao()
                    .getExpensesBetweenDates(start, end)

                withContext(Dispatchers.Main) {
                    recyclerView.adapter = ExpenseAdapter(expenses)
                }
            }
        }
    }
}