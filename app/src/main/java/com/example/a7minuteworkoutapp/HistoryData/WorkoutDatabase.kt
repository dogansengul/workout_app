package com.example.a7minuteworkoutapp.HistoryData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WorkoutEntity::class], version = 2, exportSchema = true)
abstract class WorkoutDatabase: RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: WorkoutDatabase? = null
        fun getDatabase(context: Context): WorkoutDatabase? {
            var instance = INSTANCE
            if(instance != null) {
                return instance
            } else {
                synchronized(this) {
                    val newInstance = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        "workout-database")
                        .fallbackToDestructiveMigration()
                        .build()
                    instance = newInstance
                    return instance
                }
            }
        }
    }
}