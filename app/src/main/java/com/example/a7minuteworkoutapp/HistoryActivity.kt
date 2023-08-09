package com.example.a7minuteworkoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkoutapp.HistoryData.WorkoutApp
import com.example.a7minuteworkoutapp.HistoryData.WorkoutDao
import com.example.a7minuteworkoutapp.HistoryData.WorkoutEntity
import com.example.a7minuteworkoutapp.databinding.ActivityHistoryBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {
    private var binding: ActivityHistoryBinding? = null
    private var workoutDao: WorkoutDao? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.workoutHistoryToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.workoutHistoryToolbar?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        workoutDao = (application as WorkoutApp).database?.workoutDao()
        CoroutineScope(IO).launch {
            //workoutDao?.deleteAllData()
            retrieveAllHistoryAndDisplayRV()
        }

        binding?.floatingActionButton4?.setOnClickListener {
            CoroutineScope(IO).launch {
                workoutDao?.deleteAllData()
            }
        }
    }
    private suspend fun retrieveAllHistoryAndDisplayRV() {
        workoutDao?.getAllWorkouts()?.collect{
            val workoutList = ArrayList(it)
            withContext(Main.immediate){
                setUpRecyclerView(workoutList, workoutDao!!)
            }
        }
    }
    private fun setUpRecyclerView(data: ArrayList<WorkoutEntity>, dao: WorkoutDao) {
        binding?.rvHistory?.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = HistoryAdapter(data)
            addItemDecoration(TopSpacingItemDecoration(10))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(binding != null) {
            binding = null
        }
    }
}