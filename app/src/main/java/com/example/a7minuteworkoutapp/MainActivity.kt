package com.example.a7minuteworkoutapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.a7minuteworkoutapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.startButton?.setOnClickListener {
            val intent = Intent(this, ExerciseActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }
}