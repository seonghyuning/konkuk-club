package com.example.clubapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.MessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * 채팅 Adapter
 */
class MessageAdapter(var items:ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    private lateinit var auth: FirebaseAuth

    inner class ViewHolder(val binding: MessageBinding): RecyclerView.ViewHolder(binding.root){
        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        auth = Firebase.auth

        //내가 보낸 메세지
        if(items[position].userId == auth.currentUser!!.uid) {
            holder.binding.nameText.text = ""
            holder.binding.nameText.visibility = View.GONE
            holder.binding.leftMessageText.text = ""
            holder.binding.leftCurrentDateText.text = ""
            holder.binding.leftMessageText.setBackgroundColor(Color.parseColor("#FFFFFF"))

            if(items[position].isSameDate) {
                holder.binding.rightCurrentDateText.visibility = View.INVISIBLE
            }else{
                holder.binding.rightCurrentDateText.text = items[position].createdDate
                holder.binding.rightCurrentDateText.visibility = View.VISIBLE
            }

            holder.binding.rightMessageText.text = items[position].message
            holder.binding.rightMessageText.setBackgroundResource(R.drawable.yellow_green_background)
        }
        //다른 사람이 보낸 메세지
        else{
            holder.binding.rightCurrentDateText.text = ""
            holder.binding.rightMessageText.text = ""
            holder.binding.rightMessageText.setBackgroundColor(Color.parseColor("#FFFFFF"))

            holder.binding.nameText.text = items[position].name
            holder.binding.nameText.visibility = View.VISIBLE
            holder.binding.leftMessageText.text = items[position].message

            if(items[position].isSameDate) {
                holder.binding.leftCurrentDateText.visibility = View.INVISIBLE
            }else{
                holder.binding.leftCurrentDateText.text = items[position].createdDate
                holder.binding.leftCurrentDateText.visibility = View.VISIBLE
            }

            holder.binding.leftMessageText.setBackgroundResource(R.drawable.yellow_green_background)
        }
    }
}