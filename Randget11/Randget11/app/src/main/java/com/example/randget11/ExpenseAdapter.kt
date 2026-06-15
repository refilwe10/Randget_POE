package com.example.randget11


import android.net.Uri
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class ExpenseAdapter(private val list: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val description: TextView = view.findViewById(R.id.tvDescription)
        val date: TextView = view.findViewById(R.id.tvDate)
        val image: ImageView = view.findViewById(R.id.imgExpense)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item_expense, parent, false)
        return ViewHolder(view)


    }


    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = list[position]

        holder.description.text = expense.description
        holder.date.text = expense.date

        if (expense.imageUri != null) {
            holder.image.visibility = View.VISIBLE
            holder.image.setImageURI(Uri.parse(expense.imageUri))
        } else {
            holder.image.visibility = View.GONE
        }
    }
}