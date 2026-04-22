package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider

import com.datoban.rich_uncle.util.Constants
import com.datoban.rich_uncle.visual.ViewModel.GameViewModel
import com.datoban.rich_uncle.visual.ViewModel.GameViewModelFactory
import com.datoban.rich_uncle.visual.screens.GameScreen
import com.google.firebase.auth.FirebaseAuth

class GameActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    private val roomId     by lazy { intent.getStringExtra(Constants.EXTRA_ROOM_ID) ?: "" }
    private val myPlayerId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (roomId.isEmpty()) { finish(); return }

        viewModel = ViewModelProvider(this, GameViewModelFactory())
            .get(GameViewModel::class.java)

        viewModel.loadRoom(roomId)

        setContent {
            GameScreen(
                viewModel = viewModel,
                roomId = roomId,
                playerId = myPlayerId,
                onNavigateResult = { won ->
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("WON", won)
                    intent.putExtra(Constants.EXTRA_ROOM_ID, roomId)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}