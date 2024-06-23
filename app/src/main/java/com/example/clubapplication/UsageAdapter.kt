package com.example.clubapplication

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.UsageBinding
import java.text.DecimalFormat

/**
 * 회비 사용 내역 Adapter
 */
class UsageAdapter(var items:ArrayList<Usage>): RecyclerView.Adapter<UsageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: UsageBinding): RecyclerView.ViewHolder(binding.root){
        init{
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = UsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.createdDateText.text = items[position].createdDate
        holder.binding.usageContentText.text = items[position].usageContent

        val dec = DecimalFormat("#,###")

        if(items[position].deposit > 0) {
            holder.binding.changeText.text = "+ " + dec.format(items[position].deposit) + "원"
            holder.binding.changeText.setTextColor(Color.parseColor("#0000FF"))
        }else if(items[position].withdraw > 0) {
            holder.binding.changeText.text = "- " + dec.format(items[position].withdraw) + "원"
            holder.binding.changeText.setTextColor(Color.parseColor("#FF0000"))
        }

        var rest = items[position].rest

        holder.binding.restText.text = dec.format(rest) + "원"
    }
}