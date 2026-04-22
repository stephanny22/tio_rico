package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.datoban.rich_uncle.databinding.ActivityResultBinding
import com.datoban.rich_uncle.visual.ViewModel.ResultViewModel

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private lateinit var viewModel: ResultViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding   = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ResultViewModel::class.java)

        val won = intent.getBooleanExtra("WON", false)
        binding.tvResult.text = if (won) "🏆 ¡Ganaste!" else "💸 ¡Perdiste!"

        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, LobbyActivity::class.java))
            finish()
        }

        binding.btnExit.setOnClickListener {
            finishAffinity()
        }
    }
}