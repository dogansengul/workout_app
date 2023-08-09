package com.example.a7minuteworkoutapp.HistoryData

import android.app.Application

class WorkoutApp: Application() {
    val database by lazy {
        WorkoutDatabase.getDatabase(this)
    }
}