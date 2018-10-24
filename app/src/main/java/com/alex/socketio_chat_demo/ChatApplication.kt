package com.alex.socketio_chat_demo

import android.app.Application
import io.socket.client.IO
import io.socket.client.Socket

import java.net.URISyntaxException

class ChatApplication : Application() {

    var socket: Socket? = null
        private set

    init {
        try {
            socket = IO.socket(Constants.CHAT_SERVER_URL)
        } catch (e: URISyntaxException) {
            throw RuntimeException(e)
        }

    }
}
