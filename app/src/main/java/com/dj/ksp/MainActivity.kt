package com.dj.ksp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dj.app.R
import com.dj.testannotation.TestAnnotation
import generated.file.printHackFunction

class MainActivity : AppCompatActivity() {
    @TestAnnotation
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Log--->", printHackFunction())
    }
}