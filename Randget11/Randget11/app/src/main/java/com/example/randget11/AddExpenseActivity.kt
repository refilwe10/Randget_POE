package com.example.randget11

import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.*

class AddExpenseActivity : AppCompatActivity() {

    private var selectedImageUri: String? = null

    private lateinit var db: AppDatabase
    private lateinit var categoryList: List<Category>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "budget_db"
        ).build()

        // Views
        val imageView = findViewById<ImageView>(R.id.imgPreview)
        val pickImageBtn = findViewById<Button>(R.id.btnPickImage)
        val description = findViewById<EditText>(R.id.etDescription)
        val date = findViewById<EditText>(R.id.etDate)
        val startTime = findViewById<EditText>(R.id.etStartTime)
        val endTime = findViewById<EditText>(R.id.etEndTime)
        val spinner = findViewById<Spinner>(R.id.spinnerCategory)
        val saveBtn = findViewById<Button>(R.id.btnSaveExpense)
        val amountField = findViewById<EditText>(R.id.etAmount)

        // Image Picker
        val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri.toString()
                imageView.setImageURI(uri)
            }
        }

        pickImageBtn.setOnClickListener {
            imagePicker.launch("image/*")
        }

        // Load categories into spinner
        CoroutineScope(Dispatchers.IO).launch {
            categoryList = db.categoryDao().getCategoriesByType("EXPENSE")

            val names = categoryList.map { it.name }

            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(
                    this@AddExpenseActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    names
                )
                spinner.adapter = adapter
            }
        }

        // Save Button
        saveBtn.setOnClickListener {
            val desc = description.text.toString().trim()
            val d = date.text.toString().trim()
            val start = startTime.text.toString().trim()
            val end = endTime.text.toString().trim()
            val amountText = amountField.text.toString().trim()

            if (desc.isEmpty() || d.isEmpty() || start.isEmpty() || end.isEmpty() || amountText.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountText.toDoubleOrNull()
            if (amount == null) {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedPosition = spinner.selectedItemPosition
            if (selectedPosition < 0 || selectedPosition >= categoryList.size) {
                Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val categoryId = categoryList[selectedPosition].id

            val expense = Expense(
                description = desc,
                categoryId = categoryId,
                amount = amount,
                date = d,
                startTime = start,
                endTime = end,
                imageUri = selectedImageUri
            )

            CoroutineScope(Dispatchers.IO).launch {
                db.expenseDao().insertExpense(expense)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddExpenseActivity, "Expense Saved!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}