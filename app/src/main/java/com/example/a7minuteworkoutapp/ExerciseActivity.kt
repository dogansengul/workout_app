package com.example.a7minuteworkoutapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private lateinit var exerciseList: ArrayList<ExerciseModel>
    private var currentExercisePosition = 0
    private var textToSpeech: TextToSpeech? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
            status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TTS", "Lang not supported.")
                } else {
                    speakOut()
                }
            } else {
                Log.d("TTS", "TTS not initialized.")
            }
        })
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        initExerciseStatusRecyclerView()
        exerciseList = Constants.defaultExerciseList()
        timeout()
    }

    private fun initExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.apply {
            adapter = ExerciseStatusRecyclerAdapter()
            layoutManager = LinearLayoutManager(this@ExerciseActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTimeOutScreen() {
        binding?.flProgressBar?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.flExercise1ProgressBar?.visibility = View.INVISIBLE
        binding?.exerciseImage?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.text = "Upcoming Exercise:\n${exerciseList[currentExercisePosition].getName()}"
    }
    private fun setExerciseScreen() {
        binding?.flProgressBar?.visibility = View.INVISIBLE
        binding?.tvUpcomingExercise?.visibility = View.INVISIBLE
        binding?.flExercise1ProgressBar?.visibility = View.VISIBLE
        binding?.exerciseImage?.visibility = View.VISIBLE
        binding?.exerciseImage?.setImageResource(exerciseList[currentExercisePosition].getImage())
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.tvExerciseName?.text = exerciseList[currentExercisePosition].getName()
    }
    private suspend fun startTimer(totalTime: Int, timer: TextView?, progressBar: ProgressBar?, function: () -> Unit) {
        val countdownInterval: Long = 100
        for (i in totalTime downTo 1) {
            withContext(Dispatchers.Main.immediate) {
                timer?.text = i.toString()
                progressBar?.progress = i
            }
            delay(countdownInterval)
            if (i == 1) function()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun timeout() {
        setTimeOutScreen()
        speakOut()
        lifecycleScope.launch {
            startTimer(10, binding?.timer, binding?.progressBar) {
                startExercises()
            }
        }
    }

    private fun startExercises() {
        setExerciseScreen()
        lifecycleScope.launch{
            startTimer(30, binding?.exercise1Timer, binding?.exercise1ProgressBar) {
                currentExercisePosition++
                if(currentExercisePosition == 12) {
                    openFinishActivity()
                    Toast.makeText(this@ExerciseActivity,
                        "You have completed the workout, congrats!",
                        Toast.LENGTH_SHORT).show()
                }
                else {
                    timeout()
                }
            }
        }
    }
    private fun speakOut() {
        val text = exerciseList[currentExercisePosition].getName()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun openFinishActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        textToSpeech?.shutdown()
        textToSpeech = null
    }

    override fun onPause() {
        super.onPause()
        Log.d("Onpause", "Paused")
       if(textToSpeech != null) {
           textToSpeech!!.shutdown()
           textToSpeech = null
       }
    }

    override fun onResume() {
        super.onResume()
        Log.d("Onresume", "continued")
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener {
                status ->
            if(status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.d("TTS", "Lang not supported.")
                } else {
                    speakOut()
                }
            } else {
                Log.d("TTS", "TTS not initialized.")
            }
        })
    }



    //redundant old functions
    private fun startExercise1() {
        setExerciseScreen()
        currentExercisePosition++
        lifecycleScope.launch {
            startTimer(30, binding?.exercise1Timer, binding?.exercise1ProgressBar) {
                timeout()
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
}