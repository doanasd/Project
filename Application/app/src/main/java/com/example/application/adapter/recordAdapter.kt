package com.example.application.adapter
import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.application.R
import com.example.application.currentRound
import com.example.application.kyluc
import com.example.application.record_board
import java.text.SimpleDateFormat
import java.util.Date

class recordAdapter (var context: record_board, var layout:Int, var list:ArrayList<kyluc>):
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
    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var layoutInflater = context.layoutInflater
        var view = layoutInflater.inflate(R.layout.record_item,parent,false)
        val dateFormat = SimpleDateFormat("dd:MM:yyyy")
        val date = Date()
        var tvId = view.findViewById<TextView>(R.id.id)
        var tvtg = view.findViewById<TextView>(R.id.tg)
        var tvTime = view.findViewById<TextView>(R.id.giay)
        var tvChedo = view.findViewById<TextView>(R.id.cd)
        var tvPoint = view.findViewById<TextView>(R.id.diem)
        tvtg.setText(dateFormat.format(date))
        tvId.setText(list.get(position).id.toString())
        tvTime.setText(list.get(position).tgian.toString())
        tvChedo.setText(list.get(position).chedo)
        tvPoint.setText(list.get(position).diem.toString())
        return view
    }
}