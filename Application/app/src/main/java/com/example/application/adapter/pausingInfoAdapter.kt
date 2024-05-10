package com.example.application.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.application.R
import com.example.application.currentRound
import com.example.application.pausing_form

class pausingInfoAdapter(var context: pausing_form, var layout:Int, var list:ArrayList<currentRound>):
    BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).round.toLong()
    }
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var layoutInflater = context.layoutInflater
        var view = layoutInflater.inflate(R.layout.pausing_info,parent,false)
        var tvRound = view.findViewById<TextView>(R.id.currentRound)
        var tvTime = view.findViewById<TextView>(R.id.currentTime)
        var tvChedo = view.findViewById<TextView>(R.id.chedo)
        var tvPoint = view.findViewById<TextView>(R.id.point)
        tvRound.setText(list.get(position).round.toString())
        tvTime.setText(list.get(position).time.toString())
        tvChedo.setText(list.get(position).cheDo)
        tvPoint.setText(list.get(position).diem.toString())
        return view
    }
}