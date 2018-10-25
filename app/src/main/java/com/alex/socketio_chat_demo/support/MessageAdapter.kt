package com.alex.socketio_chat_demo.support

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alex.socketio_chat_demo.R
import kotlinx.android.synthetic.main.item_message.view.*


class MessageAdapter(context: Context?, private val mMessages: List<Message>) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {
    private val mUsernameColors: IntArray = context?.resources!!.getIntArray(R.array.username_colors)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = -1
        when (viewType) {
            Message.TYPE_MESSAGE -> layout = R.layout.item_message
            Message.TYPE_LOG -> layout = R.layout.item_log
            Message.TYPE_ACTION -> layout = R.layout.item_action
        }
        val v = LayoutInflater
                .from(parent.context)
                .inflate(layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val message = mMessages[position]
        viewHolder.setMessage(message.message)
        viewHolder.setUsername(message.username)
    }

    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun getItemViewType(position: Int): Int {
        return mMessages[position].type
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        fun setUsername(username: String?) {
            if (null == username) return
            itemView.usernameView.text = username
            itemView.usernameView.setTextColor(getUsernameColor(username!!))
        }

        fun setMessage(message: String?) {
            if (null == message) return
            itemView.messageView.text = message
        }

        private fun getUsernameColor(username: String): Int {
            var hash = 7
            var i = 0
            val len = username.length
            while (i < len) {
                hash = username.codePointAt(i) + (hash shl 5) - hash
                i++
            }
            val index = Math.abs(hash % mUsernameColors.size)
            return mUsernameColors[index]
        }
    }
}
