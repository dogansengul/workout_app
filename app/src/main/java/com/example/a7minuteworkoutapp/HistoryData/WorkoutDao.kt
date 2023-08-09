package com.example.a7minuteworkoutapp.HistoryData

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWorkout(workoutEntity: WorkoutEntity)

    @Query("SELECT * FROM `workout-table` ORDER BY `primary-key` ASC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("DELETE FROM `workout-table`")
    suspend fun deleteAllData()

    @Query("SELECT COUNT(*) FROM `workout-table`")
    suspend fun getWorkoutCount(): Int


}