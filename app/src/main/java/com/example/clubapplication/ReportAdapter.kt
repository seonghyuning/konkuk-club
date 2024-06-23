package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.NoticeBinding

/**
 * 신고된 동아리 Adapter
 */
class ReportAdapter(var items:ArrayList<Report>): RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: Report, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding: NoticeBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.noticeLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
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
        holder.binding.noticeTitle.text = items[position].clubName
        holder.binding.noticeContent.text = items[position].reason
        holder.binding.createdDate.text = items[position].createdDate
    }
}