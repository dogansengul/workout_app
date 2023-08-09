package com.example.a7minuteworkoutapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.a7minuteworkoutapp.HistoryData.WorkoutApp
import com.example.a7minuteworkoutapp.HistoryData.WorkoutDao
import com.example.a7minuteworkoutapp.HistoryData.WorkoutEntity
import com.example.a7minuteworkoutapp.databinding.ActivityExerciseBinding
import com.example.a7minuteworkoutapp.databinding.DialogBackButtonBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private lateinit var exerciseList: ArrayList<ExerciseModel>
    private var currentExercisePosition = 0
    private var textToSpeech: TextToSpeech? = null
    private lateinit var rvAdapter: ExerciseStatusRecyclerAdapter

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
            showCustomBackDialog()
            //onBackPressedDispatcher.onBackPressed()
        }
        exerciseList = Constants.defaultExerciseList()
        rvAdapter = ExerciseStatusRecyclerAdapter(exerciseList)
        initExerciseStatusRecyclerView()
        timeout()
    }

    private fun initExerciseStatusRecyclerView() {
        binding?.rvExerciseStatus?.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(this@ExerciseActivity, LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(RightSpacingItemDecoration(7))
            isNestedScrollingEnabled = false
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
        val countdownInterval: Long = 10
        for (i in totalTime downTo 1) {
            withContext(Dispatchers.Main.immediate) {
                timer?.text = i.toString()
                progressBar?.progress = i
            }
            delay(countdownInterval)
            if (i == 1) function()
        }
    }
    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    private fun timeout() {
        setTimeOutScreen()
        speakOut()

        exerciseList[currentExercisePosition].setIsSelected(true)
        Log.d("durum", "$currentExercisePosition . exercise selected: ${exerciseList[currentExercisePosition].getIsSelected()}")
        Log.d("durum", "$currentExercisePosition . exercise completed: ${exerciseList[currentExercisePosition].getIsCompleted()}")

        rvAdapter.notifyDataSetChanged()

        lifecycleScope.launch {
            startTimer(10, binding?.timer, binding?.progressBar) {
                startExercises()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startExercises() {
        setExerciseScreen()
        lifecycleScope.launch{
            startTimer(30, binding?.exercise1Timer, binding?.exercise1ProgressBar) {
                exerciseList[currentExercisePosition].setIsCompleted(true)
                exerciseList[currentExercisePosition].setIsSelected(false)
                Log.d("durum", "$currentExercisePosition . exercise selected: ${exerciseList[currentExercisePosition].getIsSelected()}")
                Log.d("durum", "$currentExercisePosition . exercise completed: ${exerciseList[currentExercisePosition].getIsCompleted()}")
                rvAdapter.notifyDataSetChanged()
                currentExercisePosition++
                if(currentExercisePosition == 12) {
                    openFinishActivity()
                    lifecycleScope.launch {
                        addRecordToWorkoutHistoryDatabase()
                    }
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

    @SuppressLint("SimpleDateFormat")
    private suspend fun addRecordToWorkoutHistoryDatabase() {
        val workoutDao = (application as WorkoutApp).database?.workoutDao()
        val dateFormat = SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.getDefault())
        val currentDate = Date()
        val dateString = dateFormat.format(currentDate)
        val date = dateString.split(" ")[0]
        val time = dateString.split(" ")[1]
        Log.d("Datetime", date)
        Log.d("Datetime", time)

        val workoutCount = workoutDao?.getWorkoutCount() ?: 0
        val workoutEntity = WorkoutEntity(id = workoutCount + 1, date = date, timeStamp = time)
        workoutDao?.addWorkout(workoutEntity)
    }


    private fun speakOut() {
        val text = exerciseList[currentExercisePosition].getName()
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
    private fun openFinishActivity() {
        val intent = Intent(this, FinishActivity::class.java)
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
    private fun showCustomBackDialog() {
        val backDialog = Dialog(this)
        val dialogBinding = DialogBackButtonBinding.inflate(layoutInflater)
        backDialog.setContentView(dialogBinding.root)
        backDialog.setCanceledOnTouchOutside(true)
        dialogBinding.yesButton.setOnClickListener {
            this@ExerciseActivity.finish()
            backDialog.dismiss()
        }
        dialogBinding.noButton.setOnClickListener {
            backDialog.dismiss()
        }
        backDialog.show()
    }

    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showCustomBackDialog()
        }
    }
}