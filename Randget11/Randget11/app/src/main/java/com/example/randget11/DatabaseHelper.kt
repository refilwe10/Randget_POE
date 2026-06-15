package com.example.randget11

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object CategorySeeder {

    fun seed(context: Context, db: AppDatabase) {
        CoroutineScope(Dispatchers.IO).launch {

            val dao = db.categoryDao()

            val defaultCategories = listOf(
                Category(name = "Food", type = "EXPENSE"),
                Category(name = "Transport", type = "EXPENSE"),
                Category(name = "Rent", type = "EXPENSE"),
                Category(name = "Entertainment", type = "EXPENSE"),
                Category(name = "Shopping", type = "EXPENSE"),
                Category(name = "Health", type = "EXPENSE"),

                Category(name = "Salary", type = "INCOME"),
                Category(name = "Business", type = "INCOME"),
                Category(name = "Investments", type = "INCOME")
            )

            defaultCategories.forEach {
                dao.insertCategory(it)
            }
        }
    }
}