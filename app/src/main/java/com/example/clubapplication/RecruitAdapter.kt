package com.example.clubapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.clubapplication.databinding.RecruitBinding
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * 모집중인 동아리 Adapter
 */
class RecruitAdapter(var items:ArrayList<Club>):RecyclerView.Adapter<RecruitAdapter.ViewHolder>() {

    interface OnItemClickListener{
        fun OnItemClick(data: Club, position: Int)
    }

    var itemClickListener: OnItemClickListener?=null

    inner class ViewHolder(val binding:RecruitBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.recruitLinear.setOnClickListener {
                itemClickListener?.OnItemClick((items[adapterPosition]), adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecruitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.titleText.text = items[position].title
        holder.binding.introText.text = items[position].introduce

        if (items[position].limit == "제한없음") {
            holder.binding.boundText.text = items[position].bound
        } else {
            holder.binding.boundText.text = items[position].limit
        }

        holder.binding.typeText.text = items[position].type

        var startYear = items[position].startYear.toString()
        var startMonth = items[position].startMonth.toString()
        if(startMonth.toInt() < 10)
            startMonth = "0" + startMonth
        var startDay = items[position].startDay.toString()
        if(startDay.toInt() < 10)
            startDay = "0" + startDay

        var endYear = items[position].endYear.toString()
        var endMonth = items[position].endMonth.toString()
        if(endMonth.toInt() < 10)
            endMonth = "0" + endMonth
        var endDay = items[position].endDay.toString()
        if(endDay.toInt() < 10)
            endDay = "0" + endDay

        var startDate = startYear + "-" + startMonth + "-" + startDay
        var endDate = endYear + "-" + endMonth + "-" + endDay

        var sf = SimpleDateFormat("yyyy-MM-dd")

        var sfStartDate = sf.parse(startDate)
        var sfEndDate = sf.parse(endDate)

        var today = Calendar.getInstance()

        var calcuStartDate = (today.time.time - sfStartDate.time) / (60 * 60 * 24 * 1000)
        var calcuEndDate = (sfEndDate.time - today.time.time) / (60 * 60 * 24 * 1000)

        if(calcuStartDate >= 0 && calcuEndDate >= 0) {
            holder.binding.dDayText.text = "D-" + calcuEndDate.toString()
        }else{
            holder.binding.dDayText.text = "마감"
        }
    }
}