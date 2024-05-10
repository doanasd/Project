package com.example.application

import com.example.application.adapter.finishInfoAdapter
import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.application.databinding.FragmentFinishBoardBinding

class finish_board : Fragment() {
    lateinit var bd :FragmentFinishBoardBinding
    private lateinit var handler: Handler
    var databaseHelper: Helper? = null
    var lscround = ArrayList<currentRound>()
    var lsrecord = ArrayList<kyluc>()
    var fbAdapter = finishInfoAdapter(this,R.layout.pausing_info,lscround)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bd = FragmentFinishBoardBinding.inflate(layoutInflater)

        bd.finishInfo.adapter = fbAdapter
        fbAdapter.notifyDataSetChanged()
        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper =  Helper(context)
        databaseHelper!!.openDatabase()

        bd.home.setOnClickListener{
            bd.home.setBackgroundResource(R.drawable.homefb1)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.home.setBackgroundResource(R.drawable.homefb)
                val al = AlertDialog.Builder(context)
                al.setTitle("Exit")
                al.setMessage("Dữ liệu sẽ không được lưu nếu trở lại menu!!   Trở về?")
                al.setPositiveButton("Trở về", DialogInterface.OnClickListener { dialog, which ->
                    val activity: ManHinhGame = requireActivity() as ManHinhGame
                    activity.removeView(this)
                    activity. bd.choidon.setEnabled(true)
                    activity.bd.choidoi.setEnabled(true)
                    activity.bd.kyluc.setEnabled(true)
                })
                al.setNegativeButton("không", DialogInterface.OnClickListener { dialog, which ->
                })
                al.create().show()
            }
            handler.postDelayed(runnable, 100)
        }
        bd.save.setOnClickListener{
            bd.save.setBackgroundResource(R.drawable.savebt1)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.save.setBackgroundResource(R.drawable.savebt)
                val al = AlertDialog.Builder(context)
                al.setTitle("SAVE !")
                al.setMessage("Đồng ý lưu dữ liệu ?")
                al.setPositiveButton("Đồng ý", DialogInterface.OnClickListener { dialog, which ->
                    val contentValues = ContentValues()
                    contentValues.put("id", lsrecord.get(0).id)
                    contentValues.put("idUser", lsrecord.get(0).userId)
                    contentValues.put("userName", lsrecord.get(0).name)
                    contentValues.put("idOpn", lsrecord.get(0).idOpn)
                    contentValues.put("type", lsrecord.get(0).chedo)
                    contentValues.put("level", lsrecord.get(0).dokho)
                    contentValues.put("time", lsrecord.get(0).tgian)
                    contentValues.put("point", lsrecord.get(0).diem)

                    try {
                        databaseHelper!!.insertData("record",contentValues)
                        Toast.makeText(context, "thêm thành công!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "lỗi!", Toast.LENGTH_SHORT).show()
                    }
                    val activity: ManHinhGame = requireActivity() as ManHinhGame
                    activity. bd.choidon.setEnabled(true)
                    activity.bd.choidoi.setEnabled(true)
                    activity.bd.kyluc.setEnabled(true)
                    activity.bd.dropdown.setEnabled(true)
                    activity.removeView(this)

                })
                al.setNegativeButton("không", DialogInterface.OnClickListener { dialog, which ->
                })
                al.create().show()
            }
            handler.postDelayed(runnable, 100)
        }
    }
}