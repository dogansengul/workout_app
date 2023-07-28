package com.example.a7minuteworkoutapp

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.example.a7minuteworkoutapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        binding?.startButton?.setOnClickListener {
            playSound()
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }

    }
    private fun playSound() {
        try {
            mediaPlayer = MediaPlayer.create(applicationContext, R.raw.press_start)
            mediaPlayer?.isLooping = false
            mediaPlayer?.start()
            if(mediaPlayer!!.isPlaying) {
                Log.d("Player", "Started.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        binding = null
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }
}