package com.alex.socketio_chat_demo.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.Fragment
import android.view.*
import com.alex.socketio_chat_demo.R
import io.socket.client.Socket

/**
 * .
 * @author <a href="mailto:parselife@gmail.com">alex</a>
 * @version v1.0, 2018/10/24
 */
class MainFragment:Fragment() {

    companion object {
        const val TYPING_TIMER_LENGTH = 600
        const val REQUEST_LOGIN=0
    }

    private var mMessages = listOf<Message>()
    var mTyping = false
    var mTypingHandler = Handler()

    var mUsername:String?=null
    var mSocket:Socket?=null

    override fun getView(): View? {
        return super.getView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
    }
}