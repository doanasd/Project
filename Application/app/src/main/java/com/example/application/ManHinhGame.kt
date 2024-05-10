package com.example.application

import ProgressDialogSingleton
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.application.databinding.ActivityManHinhGameBinding
import android.net.ConnectivityManager
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import java.net.ServerSocket
import java.net.Socket

class ManHinhGame : AppCompatActivity() {
    lateinit var bd: ActivityManHinhGameBinding
    val fragmentManager = supportFragmentManager
    private lateinit var handler: Handler
    var curRound = 0
    var curTime = 0
    var curPoint = 0
    var cheDo =""
    var dk = ""
    var id = 0
    var kyluc = 0
    var mail = ""
    var url = ""
    var nname =""
    var port =0
    var serverSocket:ServerSocket? = null
    var clientSocket :Socket? = null
    var socket:Socket? = null
    var link = ""
    val mh = man_hinh_choi_game_layout()
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityManHinhGameBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        ProgressDialogSingleton.dismissProgressDialog()
        val headerView = layoutInflater.inflate(R.layout.header_layout, bd.navigationView, false)
        val avatarImageView = headerView.findViewById<ImageView>(R.id.avt)
        val nicknameTextView = headerView.findViewById<TextView>(R.id.nickname)
        val mailTextView = headerView.findViewById<TextView>(R.id.gmail)
        nname = intent.getStringExtra("nickname").toString()
         mail = intent.getStringExtra("gmail").toString()
         url = intent.getStringExtra("image").toString()
        kyluc = intent.getStringExtra("kyluc")!!.toInt()
        id = intent.getStringExtra("id")!!.toInt()
        mailTextView.setText(mail)
        Glide.with(this).load(url).into(avatarImageView)
        nicknameTextView.setText(nname)
        bd.navigationView.addHeaderView(headerView)
        setSupportActionBar(bd.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            bd.drawerLayout,
            bd.toolbar,
            R.string.navi_open,
            R.string.navi_close
        )
        bd.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        val dokho = resources.getStringArray(R.array.dokho)
        val arrAdapter = ArrayAdapter(this,R.layout.dokho_textview,dokho)
        bd.autoCompleteTextView.setAdapter(arrAdapter)
        bd.pause.setEnabled(false)


        //set anh
        avatarImageView.setOnClickListener {

        }
        //CHE DO CHOI DON
        bd.choidon.setOnClickListener() {
            dk = bd.autoCompleteTextView.text.toString()
            if(dk.equals("Độ khó")){
                Toast.makeText(this,"Chọn độ khó ",Toast.LENGTH_SHORT).show()
                bd.autoCompleteTextView.setError("lỗi")
            }else {

                val bundle = Bundle()
                mh.arguments = bundle
                bundle.putString("chedo", "chơi đơn")
                bundle.putString("dokho", dk)
                bundle.putString("avt", url)
                mh.lstRound.shuffle()
                mh.round = 1
                bd.choidon.setBackgroundResource(R.drawable.choidon2)
                handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    bd.choidon.setBackgroundResource(R.drawable.choidon)
                    fragmentManager!!.beginTransaction()
                        .add(R.id.frameMain, mh, "mh")
                        .addToBackStack(null)
                        .commit()

                }
                handler.postDelayed(runnable, 300)
                bd.pause.setEnabled(true)
                bd.dropdown.setEnabled(false)
                bd.choidon.setEnabled(false)
                bd.choidoi.setEnabled(false)
                bd.kyluc.setEnabled(false)
            }
        }


        //HANDLE BACKSTACK , RESUME VA STOP TIME
        fragmentManager.addOnBackStackChangedListener {

            Toast.makeText(this,fragmentManager.backStackEntryCount.toString(),Toast.LENGTH_SHORT).show()
            if(fragmentManager.backStackEntryCount == 1){
                mh.resumeCountdown()
            }
            if(fragmentManager.backStackEntryCount == 0){
                bd.pause.setEnabled(false)
                bd.choidon.setEnabled(true)
                bd.choidoi.setEnabled(true)
                bd.kyluc.setEnabled(true)
                bd.dropdown.setEnabled(true)
                bd.navigationBar.visibility = View.VISIBLE
                bd.bottom.visibility = View.INVISIBLE
            }

        }


        //SET ERROR CUA DROPDOWM MENU
        bd.autoCompleteTextView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            bd.autoCompleteTextView.setError(
                null
            )
        })




        //CHE DO CHOI DOI
        val cr = createRoom()
        bd.choidoi.setOnClickListener{
            val bundle = Bundle()
            cr.arguments = bundle
            bundle.putString("tenphong", nname)
            if(checkNetworkConnection()){
                bd.choidoi.setBackgroundResource(R.drawable.choidoi2)
                handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    bd.choidoi.setBackgroundResource(R.drawable.choidoi)
                    fragmentManager!!.beginTransaction()
                        .add(R.id.frameMain, cr,"cr")
                        .commit()
                }
                handler.postDelayed(runnable, 100)
                bd.dropdown.setEnabled(false)
                bd.bottom.menu.findItem(R.id.create).isEnabled = false
                bd.bottom.menu.findItem(R.id.invite).isEnabled = true
                bd.choidon.setEnabled(false)
                bd.choidoi.setEnabled(false)
                bd.kyluc.setEnabled(false)
            }
        }



        //HIEN THI KY LUC
        bd.kyluc.setOnClickListener {
            bd.pause.setEnabled(false)
            bd.choidon.setEnabled(false)
            bd.choidoi.setEnabled(false)
            bd.kyluc.setEnabled(false)
            bd.dropdown.setEnabled(false)
            val bundle = Bundle()
            val record = record_board()
            record.arguments = bundle
            bd.kyluc.setBackgroundResource(R.drawable.kyluc2)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.kyluc.setBackgroundResource(R.drawable.kyluc)
                fragmentManager!!.beginTransaction()
                    .add(R.id.frameMain, record,"record")
                    .commit()
                bundle.putString("id", id.toString())
            }
            handler.postDelayed(runnable, 10)
        }

        //NUT PÁUSE
        bd.pause.setOnClickListener{
            bd.pause.setEnabled(false)
            mh.pauseCountdown()
            val bundle = Bundle()
            val pauseForm = pausing_form()
            pauseForm.arguments = bundle
            bd.pause.setBackgroundResource(R.drawable.stopbt1)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.pause.setBackgroundResource(R.drawable.stopbt)
                fragmentManager!!.beginTransaction()
                    .add(R.id.frameMain, pauseForm,"pause")
                    .commit()
                curRound = mh.round
                curTime = mh.time
                cheDo = mh.chedo
                curPoint = mh.point
                bundle.putString("round", curRound.toString())
                bundle.putString("time", curTime.toString())
                bundle.putString("type", cheDo)
                bundle.putString("point", curPoint.toString())
            }
            handler.postDelayed(runnable, 300)

        }



        //BOTTOM MENU
        var ff = findFriend()
        bd.bottom.menu.findItem(R.id.create).isEnabled = false
        bd.bottom.setOnItemSelectedListener {
            var id = it.itemId
            when(id){
                R.id.create-> {
                    handleMenuItemClick(it)
                    replaceView(cr)
//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.frameMain,cr)
//                        commit()
//                    }

                    true
                }
                R.id.invite-> {
                    handleMenuItemClick(it)
                    replaceView(ff)
                    ff.lroom.clear()
//                    supportFragmentManager.beginTransaction().apply {
//                        replace(R.id.frameMain,ff)
//                        commit()
//
//                    }

                    true
                }

            }

            false
        }
    }


    private fun handleMenuItemClick(menuItem: MenuItem) {
        if (menuItem.itemId == R.id.invite) {
           bd.bottom.menu.findItem(R.id.create).isEnabled = true
           bd.bottom.menu.findItem(R.id.invite).isEnabled = false
        }else{
            if (menuItem.itemId == R.id.create) {
                bd.bottom.menu.findItem(R.id.invite).isEnabled = true
                bd.bottom.menu.findItem(R.id.create).isEnabled = false
            }
        }

    }
    fun completed(mh:Fragment,round:Int,time:Int,type:String,point:Int,opnId:Int,dk:Int){
        val fb = finish_board()
        fb.lscround.add(currentRound(round,time,type,point))
        fb.lsrecord.add(kyluc(kyluc+1,id,intent.getStringExtra("nickname").toString(),opnId,type,dk,time,point))
        bd.pause.setEnabled(false)
        bd.choidon.setEnabled(false)
        bd.choidoi.setEnabled(false)
        bd.kyluc.setEnabled(false)

        fragmentManager.beginTransaction()
            .remove(mh)
            .add(R.id.frameMain, fb,"finish")
            .commit()
    }
    fun removeView(view: Fragment){
        fragmentManager.beginTransaction()
            .remove(view)
            .commit()
    }
    fun replaceView(view: Fragment){
        fragmentManager.beginTransaction()
            .replace(R.id.frameMain,view)
            .commit()

    }
    fun addView(view: Fragment){
        fragmentManager.beginTransaction()
            .add(R.id.frameMain,view)
            .commit()

    }
    private fun checkNetworkConnection():Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        if (networkInfo == null || !networkInfo.isConnected) {
           Toast.makeText(this,"chưa kết nối internet",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.frameMain)
        if (fragment != null) {
            bd.pause.setEnabled(false)
            bd.choidon.setEnabled(true)
            bd.choidoi.setEnabled(true)
            bd.kyluc.setEnabled(true)
            bd.dropdown.setEnabled(true)
            bd.navigationBar.visibility = View.VISIBLE
            bd.bottom.visibility = View.INVISIBLE
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

}
