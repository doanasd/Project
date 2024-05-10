package com.example.application.adapter
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.example.application.R
import com.example.application.currentRound
import com.example.application.finish_board
import com.example.application.pausing_form

class finishInfoAdapter (var context: finish_board, var layout:Int, var list:ArrayList<currentRound>):
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
    @SuppressLint("MissingInflatedId", "ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var layoutInflater = context.layoutInflater
        var view = layoutInflater.inflate(R.layout.pausing_info,parent,false)

        var loutMan = view.findViewById<LinearLayout>(R.id.manLinearLayout)
        var tvTime = view.findViewById<TextView>(R.id.currentTime)
        var tvChedo = view.findViewById<TextView>(R.id.chedo)
        var tvPoint = view.findViewById<TextView>(R.id.point)
        loutMan.visibility = View.GONE
        tvTime.setText(list.get(position).time.toString()+"s")
        tvChedo.setText(list.get(position).cheDo)
        tvPoint.setText(list.get(position).diem.toString())
        return view
    }
}