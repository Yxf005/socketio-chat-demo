package com.alex.socketio_chat_demo.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.*
import android.widget.Toast
import com.alex.socketio_chat_demo.ChatApplication
import com.alex.socketio_chat_demo.Constants
import com.alex.socketio_chat_demo.R
import com.alex.socketio_chat_demo.support.Message
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject

/**
 * .
 * @author <a href="mailto:parselife@gmail.com">alex</a>
 * @version v1.0, 2018/10/24
 */
class MainFragment : Fragment() {

    companion object {
        const val TYPING_TIMER_LENGTH = 600
        const val REQUEST_LOGIN = 0
        const val TAG = "MainFragment"
    }

    private var mMessages = arrayListOf<Message>()
    var mTyping = false
    var mTypingHandler = Handler()

    private var mUsername: String? = null
    private var mSocket: Socket? = null
    private var isConnected: Boolean = true

    private var mAdapter: RecyclerView.Adapter<*>? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val app = activity!!.application as ChatApplication
        mSocket = app.socket
        mSocket?.run {
            on(Socket.EVENT_CONNECT, onConnect)
            on(Socket.EVENT_DISCONNECT, onDisconnect)
            on(Socket.EVENT_CONNECT_ERROR, onConnectError)
            on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            on(Constants.EVENT_NEW_MESSAGE, onNewMessage)
            on(Constants.EVENT_USER_JOINED, onUserJoined)
            on(Constants.EVENT_USER_LEFT, onUserLeft)
            on(Constants.EVENT_TYPING, onTyping)
            on(Constants.EVENT_STOP_TYPING, onStopTyping)
            connect()
        }

        // 打开用户登录页
        startSignIn()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket?.disconnect()
        mSocket?.run {
            off(Socket.EVENT_CONNECT, onConnect)
            off(Socket.EVENT_DISCONNECT, onDisconnect)
            off(Socket.EVENT_CONNECT_ERROR, onConnectError)
            off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError)
            off(Constants.EVENT_NEW_MESSAGE, onNewMessage)
            off(Constants.EVENT_USER_JOINED, onUserJoined)
            off(Constants.EVENT_USER_LEFT, onUserLeft)
            off(Constants.EVENT_TYPING, onTyping)
            off(Constants.EVENT_STOP_TYPING, onStopTyping)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return super.onOptionsItemSelected(item)
    }

    private fun startSignIn() {
        mUsername = null
        val intent = Intent(activity, LoginActivity::class.java)
        startActivityForResult(intent, REQUEST_LOGIN)
    }

    private val onConnect = Emitter.Listener {
        activity!!.runOnUiThread {
            if (!isConnected) {
                if (null != mUsername)
                    mSocket?.emit(Constants.EVENT_ADD_USER, mUsername)
                Toast.makeText(activity!!.applicationContext,
                        R.string.connected, Toast.LENGTH_LONG).show()
                isConnected = true
            }
        }
    }

    private val onDisconnect = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.i(TAG, "diconnected")
            isConnected = false
            Toast.makeText(activity!!.applicationContext,
                    R.string.disconnect, Toast.LENGTH_LONG).show()
        }
    }

    private val onConnectError = Emitter.Listener {
        activity!!.runOnUiThread {
            Log.e(TAG, "Error connecting")
            Toast.makeText(activity!!.applicationContext,
                    R.string.error_connect, Toast.LENGTH_LONG).show()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val message: String
            try {
                username = data.getString("username")
                message = data.getString("message")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            removeTyping(username)
            addMessage(username, message)
        })
    }

    private val onUserJoined = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addLog(resources.getString(R.string.message_user_joined, username))
            addParticipantsLog(numUsers)
        })
    }

    private val onUserLeft = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            val numUsers: Int
            try {
                username = data.getString("username")
                numUsers = data.getInt("numUsers")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addLog(resources.getString(R.string.message_user_left, username))
            addParticipantsLog(numUsers)
            removeTyping(username)
        })
    }

    private val onTyping = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            try {
                username = data.getString("username")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            addTyping(username)
        })
    }

    private val onStopTyping = Emitter.Listener { args ->
        activity!!.runOnUiThread(Runnable {
            val data = args[0] as JSONObject
            val username: String
            try {
                username = data.getString("username")
            } catch (e: JSONException) {
                Log.e(TAG, e.message)
                return@Runnable
            }

            removeTyping(username)
        })
    }

    private val onTypingTimeout = Runnable {
        if (!mTyping) return@Runnable
        mTyping = false
        mSocket?.emit(Constants.EVENT_STOP_TYPING)
    }

    private fun addLog(message: String) {
        mMessages.add(Message.Builder(Message.TYPE_LOG)
                .message(message).build())
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun addParticipantsLog(numUsers: Int) {
        addLog(resources.getQuantityString(R.plurals.message_participants, numUsers, numUsers))
    }

    private fun addMessage(username: String, message: String) {
        mMessages.add(Message.Builder(Message.TYPE_MESSAGE)
                .username(username).message(message).build())
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun addTyping(username: String) {
        mMessages.add(Message.Builder(Message.TYPE_ACTION)
                .username(username).build())
        mAdapter.notifyItemInserted(mMessages.size - 1)
        scrollToBottom()
    }

    private fun removeTyping(username: String) {
        for (i in mMessages.indices.reversed()) {
            val message = mMessages[i]
            if (message.type == Message.TYPE_ACTION && message.username.equals(username)) {
                mMessages.removeAt(i)
                mAdapter.notifyItemRemoved(i)
            }
        }
    }


}