package com.example.application

import com.example.application.adapter.SocketSingleton
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.application.databinding.FragmentFindFriendBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL

class findFriend : Fragment(),recycleViewRoom.OnItemClickListener  {
lateinit var bd:FragmentFindFriendBinding
    private lateinit var adapter: recycleViewRoom
    var lroom = ArrayList<roomItem>()
    var port = 0
    var urlImage = ""
    var opnId = 0
    var ip = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bd = FragmentFindFriendBinding.inflate(layoutInflater)

        return bd.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bd.seach.queryHint = "tìm bằng IP phòng"
        bd.recycleView.layoutManager = LinearLayoutManager(context)
        adapter = recycleViewRoom(lroom,this)
        bd.recycleView.adapter = adapter
        val activity: ManHinhGame = requireActivity() as ManHinhGame
        activity.bd.navigationBar.visibility = View.GONE


        bd.seach.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextSubmit(query: String?): Boolean {
                 ip = bd.seach.query.toString()
                    port = bd.port.text.toString().toInt()

                Thread {
                    try {

                         val socket = Socket("${query}", port)
                        //set singleton socket
                        SocketSingleton.setSocket(socket)
                        //nhan du lieu tu server
                        receiveFromServer(SocketSingleton.getSocket()!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }

        })

    }

    fun isURL(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun receiveFromServer(socket: Socket) {
            try {
                val activity: ManHinhGame = requireActivity() as ManHinhGame
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                while (true) {
                    val id = reader.readLine()?.toIntOrNull()
                    val ip = reader.readLine()
                    val nickname = reader.readLine()
                     val url = reader.readLine()
                    if (id != null && ip != null && nickname != null && url != null) {
                        opnId = id
                        urlImage = url
                       val opn = opnSocketInfo(id,ip,nickname,url)
                        if (isURL(opn.img)) {
                            requireActivity().runOnUiThread() {
                                lroom.add(roomItem(opn.img,opn.nickname,opn.ip))
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            requireActivity().runOnUiThread() {
                                Toast.makeText(
                                    context,
                                    "${opn.img} not a valid URL",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                    val message = reader.readLine()
                    if (message == "accept") {
                        requireActivity().runOnUiThread {
                            bd.roomLayout.visibility =View.VISIBLE
                            bd.findLayout.visibility =View.GONE
                            bd.roomName.setText(lroom.get(0).name+"'s Room")
                            bd.ip.setText(lroom.get(0).ip)
                            bd.cong.setText(port.toString())
                            Glide.with(this).load(activity.url).into(bd.avt)
                            Glide.with(this).load(lroom.get(0).imageUrl).into(bd.avt2)
                            bd.state.setText("ready...")
                            bd.name.setText(activity.nname)
                            bd.name2.setText(lroom.get(0).name)
                        }
                    }
                    val start = reader.readLine()
                    if (start == "start") {
                        requireActivity().runOnUiThread {
                            val bundle = Bundle()
                            activity.mh.arguments = bundle
                            bundle.putString("chedo", "chơi đôi")
                            bundle.putString("player", "2")
                            bundle.putString("img", urlImage)
                            bundle.putString("id", opnId.toString())
                            bundle.putString("ip", ip)
                            activity.mh.lstRound.shuffle()
                            activity.mh.round = 1
                            activity.bd.pause.setEnabled(true)
                            activity.bd.navigationBar.visibility = View.VISIBLE
                            activity.bd.bottom.visibility = View.GONE
                            activity.replaceView(activity.mh)
                            activity.removeView(this)
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
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
    override fun onItemClick(position: Int) {
        if ( SocketSingleton.getSocket() != null && SocketSingleton.getSocket() !!.isConnected) { // Kiểm tra xem socket đã được khởi tạo và kết nối chưa
            Thread {
                try {
                    sendParametersToServer(SocketSingleton.getSocket()!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } else {
            Toast.makeText(context, "Socket is not initialized or not connected", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendParametersToServer(socket: Socket) {
        try {
            val writer = OutputStreamWriter(socket.getOutputStream())
            val activity: ManHinhGame = requireActivity() as ManHinhGame
            val id = activity.id
            val ip = layip()
            val nickname = activity.nname // Tên client
            val url = activity.url
            writer.write("$id\n")
            writer.write("$ip\n")
            writer.write("$nickname\n")
            writer.write("$url\n")
            writer.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}