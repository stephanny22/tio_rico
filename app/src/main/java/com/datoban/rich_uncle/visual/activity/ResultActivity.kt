package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.datoban.rich_uncle.visual.ViewModel.ResultViewModel
import com.datoban.rich_uncle.visual.screens.ResultScreen

class ResultActivity : ComponentActivity() {

    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ResultViewModel::class.java]

        val won = intent.getBooleanExtra("WON", false)

        setContent {
            ResultScreen(
                won = won,
                onPlayAgain = {
                    startActivity(Intent(this, LobbyActivity::class.java))
                    finish()
                },
                onExit = {
                    finishAffinity()
                }
            )
        }
    }
}