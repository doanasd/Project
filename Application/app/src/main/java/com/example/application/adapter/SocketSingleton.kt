package com.example.application.adapter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.application.ManHinhGame
import com.example.application.opnSocketInfo
import com.example.application.roomItem
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket

object SocketSingleton {
    private var socket: Socket? = null
    private var clientSocket: Socket? = null
    private var serverSocket: ServerSocket? = null

    fun setSocket(sock: Socket) {
        if (socket == null) {
            socket = sock
        }
    }

    fun getSocket(): Socket? {
        return socket
    }
    fun setSocketServer(port:Int) {
        if (serverSocket == null) {
            serverSocket = ServerSocket(port)
        }
    }

    fun getSocketServer(): ServerSocket? {
        return serverSocket
    }

    fun setCLientSocket(sock: Socket) {
        if(clientSocket == null){
            clientSocket = sock
        }
    }

    fun getCLientSocket(): Socket? {
        return clientSocket
    }

}