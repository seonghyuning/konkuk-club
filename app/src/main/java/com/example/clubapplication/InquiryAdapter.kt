package com.example.clubapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.InquiryBinding

/**
 * 1:1 문의 Adapter
 */
class InquiryAdapter(var items:ArrayList<Inquiry>):RecyclerView.Adapter<InquiryAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: Inquiry, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding:InquiryBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.inquiryLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = InquiryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.inquiryContent.text = items[position].lastMessage
        holder.binding.createdDate.text = items[position].createdDate

        if(items[position].checkCount > 0) {
            holder.binding.checkCountText.text = items[position].checkCount.toString()
            holder.binding.checkCountText.visibility = View.VISIBLE
        }else{
            holder.binding.checkCountText.visibility = View.GONE
        }
    }
}