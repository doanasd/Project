package com.example.application

import com.example.application.adapter.dapAnAdapter
import com.example.application.adapter.pausingInfoAdapter
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.application.databinding.FragmentManHinhChoiGameLayoutBinding
import com.example.application.databinding.FragmentPausingFormBinding

class pausing_form : Fragment() {
    lateinit var bd: FragmentPausingFormBinding
    private lateinit var handler: Handler
    var lscround = ArrayList<currentRound>()
    var pauAdapter = pausingInfoAdapter(this,R.layout.pausing_info,lscround)
    private lateinit var sharedViewModel: viewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bd = FragmentPausingFormBinding.inflate(layoutInflater)
        return bd.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(viewModel::class.java)
        lscround.clear()

        val bundle = arguments
        if (bundle != null) {
            val curRound = bundle.getString("round")
            val curTime = bundle.getString("time")
            val type = bundle.getString("type")
            val point = bundle.getString("point")
            lscround.add(currentRound(curRound!!.toInt(),curTime!!.toInt(),type.toString(),point!!.toInt()))
        }
        bd.pausingInfo.adapter = pauAdapter
        pauAdapter.notifyDataSetChanged()

        bd.resume.setOnClickListener(){
            bd.resume.setBackgroundResource(R.drawable.exit1)
            handler = Handler(Looper.getMainLooper())

            val runnable = Runnable {
                bd.resume.setBackgroundResource(R.drawable.exit)

                val activity: ManHinhGame = requireActivity() as ManHinhGame
                activity.bd.pause.setEnabled(true)
                sharedViewModel.resumeCountdown()
                activity.removeView(this)

            }
            handler.postDelayed(runnable, 300)

        }
        bd.home.setOnClickListener(){
            bd.home.setBackgroundResource(R.drawable.home4)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.home.setBackgroundResource(R.drawable.home3)
                val al = AlertDialog.Builder(context)
                al.setTitle("Exit")
                al.setMessage("Dữ liệu sẽ không được lưu nếu trở lại menu!!   Trở về?")
                al.setPositiveButton("Trở về", DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(context, ManHinhGame::class.java)
                    startActivity(intent)
                })
                al.setNegativeButton("không", DialogInterface.OnClickListener { dialog, which ->
                })
                al.create().show()
            }
            handler.postDelayed(runnable, 100)
        }
            }
}