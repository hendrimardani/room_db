package com.example.roomdb

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdb.Databases.EmployeeEntity
import com.example.roomdb.databinding.ItemAdapterBinding

class MainAdapter(
    val items: ArrayList<EmployeeEntity>,
    val updateListener: (id: Int) -> Unit,
    val deleteListener: (id: Int) -> Unit
    ): RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemAdapterBinding): RecyclerView.ViewHolder(binding.root) {
        val llItem = binding.llItem
        val tvName = binding.tvName
        val tvEmail = binding.tvEmail
        val ivEdit = binding.ivEdit
        val ivDelete = binding.ivDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAdapterBinding
            .inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        val item = items[position]

        holder.tvName.text = item.name
        holder.tvEmail.text = item.email

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.light_gray))
        } else {
            holder.llItem.setBackgroundColor(ContextCompat
                .getColor(holder.itemView.context, R.color.white))
        }

        holder.ivEdit.setOnClickListener {
            updateListener.invoke(item.id)
        }
        holder.ivDelete.setOnClickListener {
            deleteListener.invoke(item.id)
        }
    }
}