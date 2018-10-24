package com.alex.socketio_chat_demo.view

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.alex.socketio_chat_demo.R

class MainActivity : AppCompatActivity(),MainFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
