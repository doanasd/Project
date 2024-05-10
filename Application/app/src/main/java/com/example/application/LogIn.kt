package com.example.application

import ProgressDialogSingleton
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.application.databinding.ActivityLogInBinding


class LogIn : AppCompatActivity() {
    lateinit var bd: ActivityLogInBinding
    var databaseHelper: Helper? = null
    lateinit var c: Cursor
    var id = 0
    var avt = ""
    var mail= ""
    var nname = ""
    val players = mutableListOf<player>()
    var demKiLuc = 0
    private lateinit var handler: Handler
 fun checkAcc(acc:String):Boolean {
     players.clear()
     c = databaseHelper!!.query("player", null, null, null,
         null, null, null)

     while (c.moveToNext()) {
         val player = player(c.getString(0).toInt(),c.getString(1).toString(), c.getString(2).toString(), c.getString(3).toString(),
             c.getString(4),c.getString(5))
         players.add(player)
     }

         players.forEach { player ->
             if (acc.equals(player.name + player.pass)) {
                 id = player.id
                 nname = player.nickname
                 avt = player.img
                 mail = player.email
                 return true
             }
     }

     return false
 }


    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityLogInBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)
        databaseHelper =  Helper(this)
        databaseHelper!!.openDatabase()

        ProgressDialogSingleton.dismissProgressDialog()
        bd.userName.setText("doanasd")
        bd.password.setText("123")

        bd.back.setOnClickListener(){
            bd.back.setBackgroundResource(R.drawable.back2)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.back.setBackgroundResource(R.drawable.backbt)
            }
            handler.postDelayed(runnable, 100)
        }
        bd.login.setOnClickListener{
            bd.login.setBackgroundResource(R.drawable.login2)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.login.setBackgroundResource(R.drawable.lgbt)

            }
            handler.postDelayed(runnable, 100)


            var acc = ""+ bd.userName.text+""+bd.password.text
            acc.replace(" ","")
            if( bd.userName.text.isEmpty() || bd.password.text.isBlank()){
                Toast.makeText(
                    this, "không để trống thông tin",
                    Toast.LENGTH_SHORT
                ).show()
                if( bd.userName.text.isEmpty())
                    bd.userName.setError("không để trống")
                if( bd.password.text.isEmpty())
                    bd.password.setError("không để trống")
            }
            else{
                if(checkAcc(acc)== true){
                    ProgressDialogSingleton.showProgressDialog(this,"Loading...")
                    c = databaseHelper!!.query("record", null, null, null,
                        null, null, null)
                    demKiLuc = c.count
                    val intent = Intent(this, ManHinhGame::class.java)
                    val bundle = Bundle()

                    bundle.putString("id",id.toString())
                    bundle.putString("nickname", nname)
                    bundle.putString("gmail", mail)
                    bundle.putString("image", avt)
                    bundle.putString("kyluc", demKiLuc.toString())
                    intent.putExtras(bundle)
                    startActivity(intent)
                }else {
                    bd.userName.setError("lỗi")
                    bd.password.setText("")
                    Toast.makeText(this,"Sai thông tin!!",Toast.LENGTH_SHORT).show()
                }
            }

        }
        bd.resign.setOnClickListener(){
            ProgressDialogSingleton.showProgressDialog(this,"Loanding...")
            val intent = Intent(this, Resign::class.java)
            val bundle = Bundle()
            bundle.putString("username", "")
            intent.putExtras(bundle)
            startActivityForResult(intent, 200)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 200 && resultCode == Activity.RESULT_OK){
                bd.userName.setText(data?.getStringExtra("username"))

        }
    }
}