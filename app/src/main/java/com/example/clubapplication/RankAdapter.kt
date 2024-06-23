package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.RankBinding
import java.text.DecimalFormat

/**
 * 동아리 순위 Adapter
 */
class RankAdapter(var items:ArrayList<Club>):RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: Club, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding:RankBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.rankLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.numbertext.text = (position + 1).toString()
        holder.binding.titleText.text = items[position].title
        holder.binding.introText.text = items[position].introduce

        if (items[position].limit == "제한없음") {
            holder.binding.boundText.text = items[position].bound
        } else {
            holder.binding.boundText.text = items[position].limit
        }

        holder.binding.typeText.text = items[position].type

        val dec = DecimalFormat("#,###")

        holder.binding.likeCountText.text = dec.format(items[position].likeCount)
        holder.binding.memberCountText.text = items[position].memberCount.toString() + "명 활동중"
    }
}