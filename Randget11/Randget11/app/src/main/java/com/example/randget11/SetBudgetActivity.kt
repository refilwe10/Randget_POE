package com.example.randget11

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.randget11.*
import kotlinx.coroutines.*

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budget_db"
        ).build()

        val month = findViewById<EditText>(R.id.etMonth)
        val minBudget = findViewById<EditText>(R.id.etMinBudget)
        val maxBudget = findViewById<EditText>(R.id.etMaxBudget)
        val saveBtn = findViewById<Button>(R.id.btnSaveBudget)

        saveBtn.setOnClickListener {

            val m = month.text.toString()
            val min = minBudget.text.toString()
            val max = maxBudget.text.toString()

            if (m.isEmpty() || min.isEmpty() || max.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val minValue = min.toDouble()
            val maxValue = max.toDouble()

            if (minValue > maxValue) {
                Toast.makeText(this, "Min cannot be greater than Max", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val budget = Budget(
                month = m,
                minAmount = minValue,
                maxAmount = maxValue
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.budgetDao().insertBudget(budget)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SetBudgetActivity, "Budget Saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}