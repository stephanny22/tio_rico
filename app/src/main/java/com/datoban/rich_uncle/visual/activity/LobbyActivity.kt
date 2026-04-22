package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.datoban.rich_uncle.data.model.Player
import com.datoban.rich_uncle.databinding.ActivityLobbyBinding
import com.datoban.rich_uncle.util.Constants
import com.datoban.rich_uncle.util.showToast
import com.datoban.rich_uncle.visual.ViewModel.LobbyViewModel
import com.datoban.rich_uncle.visual.ViewModel.LobbyViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class LobbyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLobbyBinding
    private lateinit var viewModel: LobbyViewModel

    private val myId   by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    private val myName by lazy { FirebaseAuth.getInstance().currentUser?.displayName ?: "Jugador" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding   = ActivityLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, LobbyViewModelFactory())
            .get(LobbyViewModel::class.java)

        viewModel.room.observe(this) { room ->
            if (room.status == "PLAYING") {
                navigateToGame(room.roomId)
            }
            viewModel.error.observe(this) { errorMsg ->
                if (errorMsg.isNotEmpty()) showToast(errorMsg)
            }
        }

        binding.btnCreateRoom.setOnClickListener {
            val roomId = binding.etRoomId.text.toString().trim()
            if (roomId.isEmpty()) { showToast("Ingresa un ID de sala"); return@setOnClickListener }
            viewModel.createRoom(roomId)
            joinAndObserve(roomId)
        }

        binding.btnJoinRoom.setOnClickListener {
            val roomId = binding.etRoomId.text.toString().trim()
            if (roomId.isEmpty()) { showToast("Ingresa un ID de sala"); return@setOnClickListener }
            joinAndObserve(roomId)
        }

        binding.btnStartGame.setOnClickListener {
            val roomId = binding.etRoomId.text.toString().trim()
            viewModel.startGame(roomId)
        }
    }

    private fun joinAndObserve(roomId: String) {
        val player = Player(
            id    = myId,
            name  = myName,
            money = Constants.INITIAL_MONEY
        )
        viewModel.joinRoom(roomId, player)
    }

    private fun navigateToGame(roomId: String) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(Constants.EXTRA_ROOM_ID, roomId)
        }
        startActivity(intent)
    }
}