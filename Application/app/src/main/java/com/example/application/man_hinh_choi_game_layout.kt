package com.example.application
import com.example.application.adapter.SocketSingleton
import com.example.application.adapter.dapAnAdapter
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.application.databinding.FragmentManHinhChoiGameLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.net.URL

class man_hinh_choi_game_layout : Fragment() {
    lateinit var bd: FragmentManHinhChoiGameLayoutBinding
    private lateinit var sharedViewModel: viewModel
    var lsDapAn = ArrayList<dapAn>()
    var lstChoose = ArrayList<dapAn>()
    var lstRound = ArrayList<round>()
    var databaseHelper: Helper? = null
    lateinit var c: Cursor
    var url: String = ""
    var a: String = ""
    var round: Int = 1
    var doKho: Int = 1
    var chedo: String = ""
    var time = 0
    var point = 1
    var roundPoint = 0
    var daAdapter = dapAnAdapter(this, R.layout.dapan_items, lsDapAn)
    var chooseAdapter = dapAnAdapter(this, R.layout.dapan_items, lstChoose)
    private var job: Job? = null
    var isPaused = false
    var player = "0"
    var opnId = 0

    fun checkDapAn(str: String, dAn: String): Boolean {
        if (dAn.equals(str)) {
            Toast.makeText(context, "DAP AN CHINH XAC!+${roundPoint}", Toast.LENGTH_SHORT).show()
            return true
        } else {
            Toast.makeText(context, "DAP AN CHUA CHINH XAC! -100 diem", Toast.LENGTH_SHORT).show()
            point -= 100
            bd.point.setText(point.toString())
            click(player)
            return false
        }
    }
    fun showHint(goiY: String, pos: Int) {
        point -= 100
        click(player)
        bd.point.setText(point.toString())
        for (i in lstChoose.indices) {

            if (lstChoose.get(i).dapan == goiY) {
                lsDapAn.get(pos).dapan = goiY
                lstChoose.get(i).dapan = ""
                break
            }
            if (i == lstChoose.size - 1) {
                Toast.makeText(context, "Kí tự ${goiY} đã được dùng!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun nextRound(dk: Int, r: Int) {
        url = lstRound.get(r - 1).url
        Glide.with(this).load(url).fitCenter().into(bd.img)
        a = lstRound.get(r - 1).da
        var kt = lstRound.get(r - 1).kiTu
        roundPoint = lstRound.get(r - 1).diem

        for (i in 0 until kt) {
            lsDapAn.add(dapAn(i, ""))
            lstChoose.add(dapAn(i, a.get(i).toString()))
            val id = a.length + i
            val randomChar = ('A' + (Math.random() * 26).toInt()).toString()
            val dapAnObj = dapAn(id, randomChar)
            lstChoose.add(dapAnObj)
        }
        lstChoose.shuffle()
        bd.tvRound.setText("Màn " + r.toString() + "/${lstRound.size}")

        if (lsDapAn.size < 10) {

            bd.lsDapAn.numColumns = lsDapAn.size
            bd.lsChoose.numColumns = lsDapAn.size
        } else {
            bd.lsDapAn.numColumns = 6
            bd.lsChoose.numColumns = 6
        }
        daAdapter.notifyDataSetChanged()
        chooseAdapter.notifyDataSetChanged()
    }

    fun tradeCharacter(pos: Int, s: String) {
        for (i in lsDapAn.indices) {
            if (lsDapAn.get(i).dapan == "") {

                lsDapAn.get(i).dapan = s
                daAdapter.notifyDataSetChanged()
                break
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity()).get(viewModel::class.java)
        val activity: ManHinhGame = requireActivity() as ManHinhGame
        activity.bd.navigationBar.visibility = View.VISIBLE
        // Quan sát LiveData để gọi hàm khi LiveData thay đổi
        sharedViewModel.resumeCountdownEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                resumeCountdown()
            }
        })
        val bundle = arguments
        if (bundle != null) {
            player = bundle.getString("player").toString()
            var ip = bundle.getString("ip").toString()
            Glide.with(this).load(activity.url).into(bd.userImg)
            if (player.equals("2")) {
                opnId = bundle.getString("id").toString().toInt()

                Thread {
                    try {
                            receiveData(SocketSingleton.getSocket()!!)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }.start()
            } else if (player.equals("1")){

                Thread {
                        try {
                                receiveData(SocketSingleton.getCLientSocket()!!)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                }.start()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bd = FragmentManHinhChoiGameLayoutBinding.inflate(layoutInflater)
        databaseHelper = Helper(context)
        databaseHelper!!.openDatabase()
        lsDapAn.clear()
        lstChoose.clear()
        lstRound.clear()

        val bundle = arguments
        if (bundle != null) {
            chedo = bundle.getString("chedo").toString()
            player = bundle.getString("player").toString()
            var dk = bundle.getString("dokho").toString()
            var url = bundle.getString("img").toString()

            if (chedo.equals("chơi đơn")) {
                bd.InfoOpn.visibility = View.INVISIBLE
            } else {
                if (isURL(url)) {
                    Glide.with(this).load(url).into(bd.opnImg)
                }
                bd.InfoOpn.visibility = View.VISIBLE
            }
            if (dk.equals("easy")) {
                doKho = 1
            } else if (dk.equals("normal")) {
                doKho = 2
            } else if (dk.equals("hard")) {
                doKho = 3
            } else {
                doKho = 3
            }


            c = databaseHelper!!.query(
                "round", null, "doKho = ${doKho}", null, null, null, "RANDOM()"
            )
            while (c.moveToNext()) {
                lstRound.add(
                    round(
                        c.getString(0).toString().toInt(),
                        c.getString(1).toString(),
                        c.getString(2).toString(),
                        c.getString(3).toString().toInt(),
                        c.getString(4).toString().toInt(),
                        c.getString(5).toString().toInt()
                    )
                )
            }
        }
        bd.tvRound.setText("Màn " + round.toString() + "/${lstRound.size}")
        url = lstRound.get(round - 1).url
        Glide.with(this).load(url).fitCenter().into(bd.img)
        a = lstRound.get(round - 1).da
        var kt = lstRound.get(round - 1).kiTu
        roundPoint = lstRound.get(round - 1).diem
        for (i in 0 until kt) {
            lsDapAn.add(dapAn(i, ""))
            lstChoose.add(dapAn(i, a.get(i).toString()))
            val id = a.length + i
            val randomChar = ('A' + (Math.random() * 26).toInt()).toString()
            val dapAnObj = dapAn(id, randomChar)
            lstChoose.add(dapAnObj)
        }
        lstChoose.shuffle()

        lstChoose.shuffle()

        if (lsDapAn.size < 10) {

            bd.lsDapAn.numColumns = lsDapAn.size
            bd.lsChoose.numColumns = lsDapAn.size
        } else {
            bd.lsDapAn.numColumns = 6
            bd.lsChoose.numColumns = 6
        }


        bd.lsDapAn.adapter = daAdapter
        bd.lsChoose.adapter = chooseAdapter

        bd.lsChoose.setOnItemClickListener { parent, view, position, id ->
            var pos = position
            var s = lstChoose.get(position).dapan
            lstChoose.get(position).dapan = ""
            chooseAdapter.notifyDataSetChanged()
            tradeCharacter(pos, s)

        }
        bd.lsDapAn.setOnItemClickListener { parent, view, position, id ->
            for (i in lstChoose.indices) {
                if (lstChoose.get(i).dapan == "") {
                    lstChoose.get(i).dapan = lsDapAn.get(position).dapan
                    lsDapAn.get(position).dapan = ""
                    daAdapter.notifyDataSetChanged()
                    chooseAdapter.notifyDataSetChanged()
                    break
                }
            }

        }
        //HINT
        bd.hint.setOnClickListener() {
            var hint = ""
            var pos = 0
            for (i in lsDapAn.indices) {
                if (lsDapAn.get(i).dapan == "") {
                    hint = a.get(i).toString()
                    pos = i
                    if (i < lsDapAn.size) {
                        showHint(hint, pos)
                        daAdapter.notifyDataSetChanged()
                        chooseAdapter.notifyDataSetChanged()
                    }
                    break
                }
            }
        }
        //CHECK
        bd.check.setOnClickListener() {
            val ac: ManHinhGame = requireActivity() as ManHinhGame
            var da = ""
            for (i in lsDapAn.indices) {
                da += lsDapAn.get(i).dapan
            }
            if (checkDapAn(da, a) == true) {
                if (round >= lstRound.size) {
                    point += roundPoint
                    click(player)
                    val activity: ManHinhGame = requireActivity() as ManHinhGame
                    activity.completed(this, round, time, chedo, point, opnId, doKho)
                } else {
                    lsDapAn.clear()
                    lstChoose.clear()
                    round++
                    point += roundPoint
                    bd.point.setText(point.toString())
                    click(player)
                    nextRound(doKho, round)
                }

            }
        }
        //NEXT
        bd.next.setOnClickListener {
            if (round < lstRound.size) {
                lsDapAn.clear()
                lstChoose.clear()
                round += 1
                var r = round
                url = lstRound.get(r - 1).url
                Glide.with(this).load(url).fitCenter().into(bd.img)
                a = lstRound.get(r - 1).da
                var kt = lstRound.get(r - 1).kiTu
                roundPoint = lstRound.get(r - 1).diem
                Toast.makeText(context, roundPoint.toString(), Toast.LENGTH_SHORT).show()

                for (i in 0 until kt) {
                    lsDapAn.add(dapAn(i, ""))
                    lstChoose.add(dapAn(i, a.get(i).toString()))
                    val id = a.length + i
                    val randomChar = ('A' + (Math.random() * 26).toInt()).toString()
                    val dapAnObj = dapAn(id, randomChar)
                    lstChoose.add(dapAnObj)
                }
                lstChoose.shuffle()
                bd.tvRound.setText("Màn " + r.toString() + "/${lstRound.size}")

                if (lsDapAn.size < 10) {

                    bd.lsDapAn.numColumns = lsDapAn.size
                    bd.lsChoose.numColumns = lsDapAn.size
                } else {
                    bd.lsDapAn.numColumns = 6
                    bd.lsChoose.numColumns = 6
                }
                daAdapter.notifyDataSetChanged()
                chooseAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(context, "Màn cuối cùng rồi ông quái ", Toast.LENGTH_SHORT).show()
            }
        }

        return bd.root
    }

    override fun onStart() {
        super.onStart()
        startCountdown()
    }

    override fun onStop() {
        super.onStop()
        cancelCountdown()
    }

    private fun startCountdown() {
        job = CoroutineScope(Dispatchers.IO).launch {
            val initialTime = 0
            var remainingTime = initialTime

            while (true) {
                if (!isPaused) {
                    withContext(Dispatchers.Main) {
                        val minutes = (remainingTime % 3600) / 60
                        val seconds = remainingTime % 60
                        val formattedTime = String.format("%02d:%02d", minutes, seconds)
                        bd.giay.setText(formattedTime)
                        time = remainingTime
                    }
                    delay(1000L) // Delay for 1 second
                    remainingTime++
                } else {
                    delay(1000L)
                }
            }
        }
    }

    private fun cancelCountdown() {
        job?.cancel()
    }

    fun pauseCountdown(): Boolean {
        isPaused = true
        bd.lsDapAn.setOnItemClickListener(null)
        bd.lsChoose.setOnItemClickListener(null)
        return isPaused
    }

    fun resumeCountdown(): Boolean {
        isPaused = false
        bd.lsChoose.setOnItemClickListener { parent, view, position, id ->
            for (i in lsDapAn.indices) {
                if (lsDapAn.get(i).dapan == "") {

                    lsDapAn.get(i).dapan = lstChoose.get(position).dapan
                    lstChoose.get(position).dapan = ""
                    daAdapter.notifyDataSetChanged()
                    chooseAdapter.notifyDataSetChanged()
                    break
                }
            }

        }
        bd.lsDapAn.setOnItemClickListener { parent, view, position, id ->
            for (i in lstChoose.indices) {
                if (lstChoose.get(i).dapan == "") {
                    lstChoose.get(i).dapan = lsDapAn.get(position).dapan
                    lsDapAn.get(position).dapan = ""
                    daAdapter.notifyDataSetChanged()
                    chooseAdapter.notifyDataSetChanged()
                    break
                }
            }

        }
        return isPaused
    }

    fun isURL(url: String): Boolean {
        return try {
            URL(url)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun sendData(sk: Socket) {
        val diem  = point.toString()
        try {
            val writer = OutputStreamWriter(sk.getOutputStream())
            writer.write("$diem\n")
            writer.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun click(player:String) {
        if(player == "2") {
            if (SocketSingleton.getSocket() != null && SocketSingleton.getSocket()!!.isConnected) { // Kiểm tra xem socket đã được khởi tạo và kết nối chưa
                Thread {
                    try {
                        sendData(SocketSingleton.getSocket()!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Toast.makeText(
                    context,
                    "Socket is not initialized or not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }else if (player == "1"){
            if (SocketSingleton.getCLientSocket() != null && SocketSingleton.getCLientSocket()!!.isConnected) { // Kiểm tra xem socket đã được khởi tạo và kết nối chưa
                Thread {
                    try {
                        sendData(SocketSingleton.getCLientSocket()!!)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }.start()
            } else {
                Toast.makeText(
                    context,
                    "Socket is not initialized or not connected",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun receiveData(socket: Socket) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            while (true) {
                val mess = reader.readLine()
                if(mess !=null) {
                    bd.opnpoint.setText(mess)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

}

