package com.example.application

import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.application.databinding.FragmentRecordBoardBinding

class record_board : Fragment() {
lateinit var bd: FragmentRecordBoardBinding
    var lsrecord = ArrayList<kyluc>()
    private lateinit var rcAdapter: rcAdapter
    var databaseHelper: Helper? = null
    lateinit var c: Cursor
    var idUser = 0
    private lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bd = FragmentRecordBoardBinding.inflate(layoutInflater)
        databaseHelper =  Helper(context)
        databaseHelper!!.openDatabase()
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: ManHinhGame = requireActivity() as ManHinhGame
        activity.bd.pause.setEnabled(false)
        activity.bd.navigationBar.visibility = View.GONE
        val bundle = arguments

        lsrecord.clear()
        if (bundle != null) {
            idUser = bundle.getString("id")!!.toInt()
            c = databaseHelper!!.query("record", null, "idUser=${idUser}"
                , null, null, null, null)
            while (c.moveToNext()){
                lsrecord.add(kyluc(c.getString(0).toString().toInt(),c.getString(1).toString().toInt(),c.getString(2).toString(),c.getString(3).toString().toInt(),c.getString(4).toString(),c.getString(5).toString().toInt(),c.getString(6).toString().toInt(),c.getString(7).toString().toInt()))
            }

        }

        bd.rcView.layoutManager = LinearLayoutManager(context)
        rcAdapter = rcAdapter(lsrecord)
        bd.rcView.adapter = rcAdapter

        bd.exit.setOnClickListener {
            bd.exit.setBackgroundResource(R.drawable.exit1)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.exit.setBackgroundResource(R.drawable.exit)
                activity.removeView(this)
                activity.bd.choidon.setEnabled(true)
                activity.bd.choidoi.setEnabled(true)
                activity.bd.kyluc.setEnabled(true)
                activity.bd.dropdown.setEnabled(true)
                activity.bd.navigationBar.visibility = View.VISIBLE
                activity.bd.bottom.visibility = View.INVISIBLE
            }
            handler.postDelayed(runnable, 300)
        }
    }

}