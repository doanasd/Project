package com.example.application

import com.example.application.adapter.SocketSingleton
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.application.databinding.FragmentCreateRoomBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URL
import java.security.SecureRandom

class createRoom : Fragment() {
    lateinit var bd:FragmentCreateRoomBinding
    private lateinit var handler: Handler
    var urlImg =""
    var opnId = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activity: ManHinhGame = requireActivity() as ManHinhGame
        bd = FragmentCreateRoomBinding.inflate(layoutInflater)
        activity.bd.navigationBar.visibility = View.GONE
        if(activity.port == 0) {
            val minPort = 30000
            val maxPort = 60000
            val random = SecureRandom()
            val randomPort = random.nextInt(maxPort - minPort + 1) + minPort
            SocketSingleton.setSocketServer(randomPort)
            bd.port.setText(randomPort.toString())
            activity.port = randomPort
        }
        bd.port.setText(activity.port.toString())
        Thread {
            try {

                activity.clientSocket = SocketSingleton.getSocketServer()?.accept()
                if (activity.clientSocket != null) {
                    SocketSingleton.setCLientSocket(activity.clientSocket!!)

                    //gui nhan du lieu tu client
                    sendParametersToClient(SocketSingleton.getCLientSocket()!!)
                    startReceivingData(SocketSingleton.getCLientSocket()!!)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
        return bd.root
    }
    @SuppressLint("SuspiciousIndentation")
    fun sendParametersToClient(socket: Socket) {
        val activity: ManHinhGame = requireActivity() as ManHinhGame
            try {
                val writer = OutputStreamWriter(socket.getOutputStream())
                val ip = layip()
                val nickname = activity.nname
                val url = activity.url
                val id = activity.id

                writer.write("$id\n")
                writer.write("$ip\n")
                writer.write("$nickname\n")
                writer.write("$url\n")
                writer.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            }
    }
    private fun sendConfirm(socket: Socket, text:String) {
        try {
            val writer = OutputStreamWriter(socket.getOutputStream())
            val confirm = text
            writer.write("$confirm\n")
            writer.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun startReceivingData(socket: Socket) {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (true) {
                    val id = reader.readLine()?.toIntOrNull()
                    val ip = reader.readLine()
                    val nickname = reader.readLine()
                    val url = reader.readLine()
                    if (id != null && ip != null && nickname != null && url != null) {
                        opnId = id
                        urlImg = url
                        val opn = opnSocketInfo(id,ip,nickname,url)
                        if (isURL(opn.img)) {
                            requireActivity().runOnUiThread() {
                                val al = AlertDialog.Builder(context)
                                al.setTitle("confirmation")
                                al.setMessage("đối thủ +1, chấp nhận?")
                                al.setPositiveButton("chấp nhận", DialogInterface.OnClickListener { dialog, which ->
                                    bd.name2.setText(nickname)
                                    Glide.with(this).load(opn.img).into(bd.avt2)
                                    bd.state.setText("ready...")
                                    bd.start.visibility =View.VISIBLE

                                    Thread {
                                        try {
                                            sendConfirm(socket,"accept")
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            // Xử lý lỗi khi đọc dữ liệu từ server
                                        }
                                    }.start()
                                })
                                al.setNegativeButton("không", DialogInterface.OnClickListener { dialog, which ->
                                })
                                al.create().show()
                            }
                        } else {
                            requireActivity().runOnUiThread() {
                                // Hiển thị Toast thông báo lỗi
                                Toast.makeText(context, "${opn.img} not a valid URL", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: ManHinhGame = requireActivity() as ManHinhGame
        activity.bd.bottom.visibility = View.VISIBLE
        var ip = layip()
        bd.ip.setText(ip)

        val bundle = arguments
        if (bundle != null) {
            val rn = arguments?.getString("tenphong")
            bd.roomName.setText(rn+"'s room")
            Glide.with(this).load(activity.url).into(bd.avt)
            bd.name.setText(activity.nname)
        }
        bd.start.setOnClickListener {
            bd.start.setBackgroundResource(R.drawable.start1)
                Thread {
                    try {
                        sendConfirm(activity.clientSocket!!,"start")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Xử lý lỗi khi đọc dữ liệu từ server
                    }
                }.start()
                        handler = Handler(Looper.getMainLooper())
                        val runnable = Runnable {
                            bd.start.setBackgroundResource(R.drawable.start)
                        requireActivity().runOnUiThread {
                            val activity: ManHinhGame = requireActivity() as ManHinhGame
                            val bundle = Bundle()
                            activity.mh.arguments = bundle
                            bundle.putString("chedo", "chơi đôi")
                            bundle.putString("player", "1")
                            bundle.putString("img", urlImg)
                            bundle.putString("id", opnId.toString())
                            activity.mh.lstRound.shuffle()
                            activity.mh.round = 1
                            activity.bd.pause.setEnabled(true)
                            activity.bd.navigationBar.visibility = View.VISIBLE
                            activity.bd.bottom.visibility = View.GONE
                            activity.replaceView(activity.mh)
                            activity.removeView(this)
                        }


                         }
            handler.postDelayed(runnable, 1000)

        }

    }
    fun layip():String {
        var IP:String?= null
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val inetAddresses = networkInterface.inetAddresses
            while (inetAddresses.hasMoreElements()) {
                val inetAddress = inetAddresses.nextElement()
                if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                    IP= inetAddress.hostAddress.toString()
                }
            }
        }
        return IP.toString()
    }
    fun isURL(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }
}