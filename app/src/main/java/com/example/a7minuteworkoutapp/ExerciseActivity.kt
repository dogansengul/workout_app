package com.example.a7minuteworkoutapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private lateinit var exerciseList: ArrayList<ExerciseModel>
    private var currentExercisePosition = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        exerciseList = Constants.defaultExerciseList()
        binding?.tvUpcomingExercise?.text = "Upcoming Exercise:\n${exerciseList[currentExercisePosition].getName()}"
        lifecycleScope.launch {
            startTimer(10, binding?.timer, binding?.progressBar) {
                startExercise1()}
        }

    }

    private suspend fun startTimer(totalTime: Int, timer: TextView?, progressBar: ProgressBar?, function: () -> Unit) {
        val countdownInterval: Long = 100
        for (i in totalTime downTo 1) {
            withContext(Dispatchers.Main) {
                timer?.text = i.toString()
                progressBar?.progress = i
            }
            delay(countdownInterval)
            if (i == 1) function()
        }
    }

    private fun startExercise1() {
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.visibility = View.INVISIBLE
        binding?.flExercise1ProgressBar?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        binding?.exerciseImage?.setImageResource(exerciseList[currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList[currentExercisePosition].getName()
        currentExercisePosition++
        lifecycleScope.launch {
            startTimer(30, binding?.exercise1Timer, binding?.exercise1ProgressBar) {
                timeout()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun timeout() {
        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExercise1ProgressBar?.visibility = View.INVISIBLE
        binding?.exerciseImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.text = "Upcoming Exercise:\n${exerciseList[currentExercisePosition].getName()}"
        lifecycleScope.launch {
            startTimer(10, binding?.timer, binding?.progressBar) {
                startOtherExercises()
            }
        }
    }

    private fun startOtherExercises() {
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExercise1ProgressBar?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        if (currentExercisePosition <= 11) {
            binding?.exerciseImage?.setImageResource(exerciseList[currentExercisePosition].getImage())
            binding?.tvExerciseName?.text = exerciseList[currentExercisePosition].getName()
            currentExercisePosition++
            lifecycleScope.launch {
                startTimer(30, binding?.exercise1Timer, binding?.exercise1ProgressBar) {
                    if (currentExercisePosition <= 11) {
                        timeout()
                    } else {
                        openFinishActivity()
                        Toast.makeText(this@ExerciseActivity,
                            "You have completed the workout, congrats!",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun openFinishActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}