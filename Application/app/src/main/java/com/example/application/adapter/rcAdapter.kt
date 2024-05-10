package com.example.application

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date


class rcAdapter( val items: ArrayList<kyluc>) : RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.idTextView.text = item.id.toString()
        holder.cdTextView.text =item.chedo
        holder.giayTextView.text =item.tgian.toString()
        holder.diemTextView.text = item.diem.toString()
        val dateFormat = SimpleDateFormat("dd:MM:yyyy")
        val date = Date()
        holder.tgTextView.text = dateFormat.format(date)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val idTextView: TextView = itemView.findViewById(R.id.id)
    val cdTextView: TextView = itemView.findViewById(R.id.cd)
    val giayTextView: TextView = itemView.findViewById(R.id.giay)
    val diemTextView: TextView = itemView.findViewById(R.id.diem)
    val tgTextView: TextView = itemView.findViewById(R.id.tg)
}
