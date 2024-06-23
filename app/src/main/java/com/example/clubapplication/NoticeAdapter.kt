package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.NoticeBinding

/**
 * 공지 Adapter
 */
class NoticeAdapter(var items:ArrayList<Notice>): RecyclerView.Adapter<NoticeAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: NoticeBinding): RecyclerView.ViewHolder(binding.root){
        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = NoticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.noticeTitle.text = items[position].noticeTitle
        holder.binding.noticeContent.text = items[position].noticeContent
        holder.binding.createdDate.text = items[position].createdDate
    }
}