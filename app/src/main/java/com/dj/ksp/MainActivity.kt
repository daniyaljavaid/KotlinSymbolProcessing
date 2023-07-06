package com.dj.ksp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dj.app.R
import com.dj.ksp.repository.IRepository
import com.dj.ksp.usecase.IUseCase
import com.dj.ksp.viewmodel.IViewModel
import generated.file.printHackFunction
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var repository: IRepository

    @Inject
    lateinit var usecase: IUseCase

    @Inject
    lateinit var viewmodel: IViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.e("Log--->", printHackFunction())
    }
}
