package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.ManagerClubRowBinding

/**
 * 관리자 Adapter
 */
class ManagerAdapter(var items:ArrayList<Club>):RecyclerView.Adapter<ManagerAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun OnItemClick(data: Club, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding:ManagerClubRowBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.clubLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ManagerClubRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.binding.titleText.text = items[position].title
            holder.binding.IntroText.text = items[position].introduce

            if (items[position].limit == "제한없음") {
                holder.binding.boundText.text = items[position].bound
            } else {
                holder.binding.boundText.text = items[position].limit
            }

            holder.binding.typeText.text = items[position].type
    }
}