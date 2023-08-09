package com.example.a7minuteworkoutapp

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minuteworkoutapp.databinding.LayoutExerciseStatusItemBinding

class ExerciseStatusRecyclerAdapter(private var list: ArrayList<ExerciseModel>): RecyclerView.Adapter<ExerciseStatusRecyclerAdapter.ViewHolder>() {
    private val exerciseList = list

    inner class ViewHolder(private val binding: LayoutExerciseStatusItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvItem = binding.itemExerciseStatus
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutExerciseStatusItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exercise: ExerciseModel = exerciseList[position]
        holder.tvItem.text = exercise.getId().toString()

        when {
            exercise.getIsSelected() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.circular_color_accent_border_for_rv)
            }
            exercise.getIsCompleted() -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_color_primary_background)
                holder.tvItem.setTextColor(Color.WHITE)
            }
            else -> {
                holder.tvItem.background = ContextCompat.getDrawable(holder.itemView.context,
                    R.drawable.item_circular_gray_background)
            }
        }
    }
}