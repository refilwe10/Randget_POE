package com.example.randget11

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecurringExpenseAdapter(
    private val data: MutableList<RecurringExpense>,
    private val categoryNames: Map<Int, String>,
    private val onDelete: (RecurringExpense) -> Unit
) : RecyclerView.Adapter<RecurringExpenseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.tvDescription)
        val details: TextView = itemView.findViewById(R.id.tvDetails)
        val deleteBtn: Button = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recurring_expense, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val categoryName = categoryNames[item.categoryId] ?: "Unknown"

        holder.description.text = item.description
        holder.details.text = "R %.2f  |  %s  |  Day %d of each month".format(
            item.amount, categoryName, item.dayOfMonth
        )

        holder.deleteBtn.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = data.size
}
