package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.MemberBinding

/**
 * 동아리 회원 Adapter
 */
class ClubLogAdapter(var items:ArrayList<ClubLog>):RecyclerView.Adapter<ClubLogAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data: ClubLog, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding: MemberBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.memberLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.nameText.text = items[position].name
        holder.binding.collegeText.text = items[position].college
        holder.binding.majorText.text = items[position].major
        holder.binding.numberText.text = items[position].number
    }
}