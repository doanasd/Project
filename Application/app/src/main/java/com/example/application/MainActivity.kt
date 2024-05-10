package com.example.application

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.application.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    lateinit var bd:ActivityMainBinding
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityMainBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        bd.play.setOnClickListener(){
            ProgressDialogSingleton.showProgressDialog(this, "Loading...")
            bd.play.setBackgroundResource(com.example.application.R.drawable.bt1)
            handler = Handler(Looper.getMainLooper())
            val runnable = Runnable {
                bd.play.setBackgroundResource(com.example.application.R.drawable.bt)
            }
            handler.postDelayed(runnable, 30)

            val myIntent = Intent(this,LogIn::class.java)
            startActivity(myIntent)
        }
    }
}