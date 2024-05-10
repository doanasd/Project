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


class recycleViewRoom( val items: ArrayList<roomItem> ,val listener: OnItemClickListener)   : RecyclerView.Adapter<MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.room_item, parent, false)
            return MyViewHolder(view)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val item = items[position]
            holder.imageView.loadImage(item.imageUrl,holder.imageView)
            holder.nameTextView.text = item.name
            holder.iptv.text = item.ip

            holder.btIV.setOnClickListener {
                listener.onItemClick(position)
                it.setBackgroundResource(R.drawable.start1)
            }

        }
        override fun getItemCount(): Int {
            return items.size
        }
    }

class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView: ImageView = itemView.findViewById(R.id.img)
    val nameTextView: TextView = itemView.findViewById(R.id.name)
    val iptv: TextView = itemView.findViewById(R.id.ip)
    val btIV: ImageButton = itemView.findViewById<ImageButton?>(R.id.bt)

    fun setOnClickListener(listener: () -> Unit) {
        btIV.setOnClickListener {
            listener()
        }
    }
}


fun ImageView.loadImage(imageURL: String,imgv:ImageView) {
        Glide.with(context).load(imageURL).into(imgv)
    }