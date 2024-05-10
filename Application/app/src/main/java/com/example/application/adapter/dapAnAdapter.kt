package com.example.application.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.application.R
import com.example.application.dapAn
import com.example.application.man_hinh_choi_game_layout

class dapAnAdapter(var context: man_hinh_choi_game_layout, var layout: Int, var list:ArrayList<dapAn>):
    BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }
    override fun getItem(position: Int): Any {
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return list.get(position).id.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var layoutInflater = context.layoutInflater
        var view = layoutInflater.inflate(R.layout.dapan_items,parent,false)
        var tvId = view.findViewById<TextView>(R.id.dapAn)
        tvId.setText(list.get(position).dapan)

        return view
    }
}