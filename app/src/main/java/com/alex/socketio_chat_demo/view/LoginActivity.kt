package com.alex.socketio_chat_demo.view

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.alex.socketio_chat_demo.ChatApplication
import com.alex.socketio_chat_demo.Constants
import com.alex.socketio_chat_demo.R
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var mUsername: String? = null

    private var mSocket: Socket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val app = application as ChatApplication
        mSocket = app.socket

        // Set up the login form.
        text_username.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            attemptLogin()
            return@OnEditorActionListener true
        })

        join_in_button.setOnClickListener { attemptLogin() }

        mSocket?.on("login", onLogin)
    }

    private val onLogin = Emitter.Listener { args ->
        val data = args[0] as JSONObject

        val numUsers: Int
        try {
            numUsers = data.getInt("numUsers")
        } catch (e: JSONException) {
            return@Listener
        }

        val intent = Intent()
        intent.putExtra("username", mUsername)
        intent.putExtra("numUsers", numUsers)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    /**
     * Attempts to sign in the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.

        text_username.error = null

        // Store values at the time of the login attempt.
        val username = text_username.text.toString().trim({ it <= ' ' })

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            text_username.error = getString(R.string.error_field_required)
            text_username.requestFocus()
            return
        }

        mUsername = username

        // perform the user login attempt.
        mSocket?.emit(Constants.EVENT_ADD_USER, username)
    }

    override fun onDestroy() {
        super.onDestroy()

        mSocket?.off("login", onLogin)
    }
}
