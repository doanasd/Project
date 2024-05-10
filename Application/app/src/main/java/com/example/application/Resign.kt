package com.example.application

import ProgressDialogSingleton
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.application.databinding.ActivityResignBinding
import java.net.URL


class Resign : AppCompatActivity() {
    lateinit var bd: ActivityResignBinding
    var databaseHelper: Helper? = null
    lateinit var c: Cursor
    val players = mutableListOf<player>()
    var index = 1
    private lateinit var handler: Handler

    @Suppress("DEPRECATION")
    fun dangKy(ten:String, mk:String, nn:String,url:String,gmail:String){
        val contentValues = ContentValues()
        contentValues.put("id", index)
        contentValues.put("name", ten)
        contentValues.put("pass", mk)
        contentValues.put("nickname", nn)
        contentValues.put("image", url)
        contentValues.put("email", gmail)
        try {
           databaseHelper!!.insertData("player",contentValues)
            Toast.makeText(this, "thêm thành công!", Toast.LENGTH_SHORT).show()
            var username = intent.getStringExtra("username")
            val intent = Intent()
            username +=ten
            intent.putExtra("username", username)
            setResult(Activity.RESULT_OK, intent)
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "lỗi!", Toast.LENGTH_SHORT).show()
        }
    }
    fun checkAcc(acc:String):Boolean {
            players.clear()
        c = databaseHelper!!.query("player", null, null, null, null, null, null)
        while (c.moveToNext()) {
            index++
            val player = player(c.getString(0).toInt(),c.getString(1).toString(), c.getString(2).toString(), c.getString(3).toString(), c.getString(4).toString(),c.getString(5).toString())
            players.add(player)
        }
        players.forEach { player ->
            if (acc.equals(player.name + player.pass)) {
                return true
            }
        }
        return false
    }
    fun isURL(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
    private fun loadImageFromUrl(urlString: String) {
        Glide.with(this)
            .load(urlString)
            .into(bd.image)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityResignBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)
        databaseHelper =  Helper(this)
        databaseHelper!!.openDatabase()

        ProgressDialogSingleton.dismissProgressDialog()

        bd.urltext.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val urlString = s.toString()
                if(urlString == null){
                    bd.image.setBackgroundResource(R.drawable.null_avt_foreground)
                }
                if (isURL(urlString)) {
                    bd.urltext.width = 0
                    Toast.makeText(this@Resign,"Đường dẫn hợp lệ",Toast.LENGTH_SHORT).show()
                    loadImageFromUrl(urlString)
                }else{
                    bd.image.setBackgroundResource(R.drawable.null_avt_foreground)
                    Toast.makeText(this@Resign,"Đường dẫn không hợp lệ",Toast.LENGTH_SHORT).show()
                }
            }
        })

        bd.back.setOnClickListener(){
            bd.back.setBackgroundResource(R.drawable.back2)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.back.setBackgroundResource(R.drawable.backbt)
            }
            handler.postDelayed(runnable, 100)
            val myIntent = Intent(this,MainActivity::class.java)
            startActivity(myIntent)
        }
        bd.rs.setOnClickListener() {
            bd.rs.setBackgroundResource(R.drawable.resign2)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.rs.setBackgroundResource(R.drawable.rs)
            }
            handler.postDelayed(runnable, 100)
            var acc = ""+ bd.userName.text+""+bd.pass.text
            acc.replace(" ","")
            if( bd.nickname.text.isEmpty() || bd.userName.text.isEmpty() || bd.pass.text.isBlank() || bd.repass.text.equals("") || bd.urltext.text.isEmpty() || bd.gmail.text.isBlank()){
                if( bd.nickname.text.isEmpty()){
                    bd.nickname.setError("không để trống")
                }else
                if( bd.userName.text.isEmpty()){
                    bd.userName.setError("không để trống")
                }else
                if( bd.pass.text.isEmpty())
                {
                    bd.pass.setError("không để trống")
                }else
                if( bd.repass.text.isEmpty())
                {
                    bd.repass.setError("không để trống")
                }else if( bd.urltext.text.isEmpty())
                {
                    bd.urltext.setError("không để trống")
                }else if( bd.gmail.text.isEmpty())
                {
                    bd.gmail.setError("không để trống")
                }
            }
            else {
                if (bd.pass.text.toString().equals(bd.repass.text.toString())) {
                    if (checkAcc(acc) == true) {
                        Toast.makeText(this, "Tài khoản đã tồn tại!!${index}", Toast.LENGTH_SHORT).show()
                    } else {
                        if (isURL(bd.urltext.text.toString())) {
                            dangKy(
                                bd.userName.text.toString(),
                                bd.pass.text.toString(),
                                bd.nickname.text.toString(),
                                bd.urltext.text.toString(),
                                bd.gmail.text.toString()
                            )
                        }
                    }
                } else {
                    bd.repass.setError("không khớp với mật khẩu!")
                }
            }

        }
        bd.clear.setOnClickListener {
            bd.urltext.setText("")
        }

    }
}