package com.example.a7minuteworkoutapp.HistoryData

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout-table")
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("primary-key") val id: Int = 0,
    @ColumnInfo("Workout-date") val date: String,
    @ColumnInfo("workout-time") val timeStamp: String
)