package com.example.a7minuteworkoutapp

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a7minuteworkoutapp.databinding.LayoutExerciseStatusItemBinding

class ExerciseStatusRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val exerciseList = Constants.defaultExerciseList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ExerciseStatusViewHolder(LayoutExerciseStatusItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return exerciseList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ExerciseStatusViewHolder ->
                holder.bind(exerciseList[position])
        }
    }
    inner class ExerciseStatusViewHolder(private val itemBinding: LayoutExerciseStatusItemBinding): RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(exercise: ExerciseModel) {
            itemBinding.itemExerciseStatus.text = exercise.getId().toString()
        }
    }
}