package com.example.a7minuteworkoutapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minuteworkoutapp.HistoryData.WorkoutEntity
import com.example.a7minuteworkoutapp.databinding.WorkoutHistorySingleWorkoutBinding

class HistoryAdapter(private val workoutData: ArrayList<WorkoutEntity>)
    : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(private val binding: WorkoutHistorySingleWorkoutBinding)
        : RecyclerView.ViewHolder(binding.root) {
        val layout = binding.llItem
        val tvId = binding.tvId
        val tvDate = binding.tvDate
        val tvTime = binding.tvTime
        fun bind(workoutEntity: WorkoutEntity) {
            tvId.text = workoutEntity.id.toString()
            tvDate.text = workoutEntity.date
            tvTime.text = workoutEntity.timeStamp
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(WorkoutHistorySingleWorkoutBinding.inflate
            (LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return workoutData.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(workoutData[position])
        if(position % 2 == 0) {
            holder.layout.setBackgroundColor(ContextCompat.getColor
                (holder.itemView.context, R.color.lightGrey))
        }
    }
}