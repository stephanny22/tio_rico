package com.datoban.rich_uncle.visual.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity          // ← resuelve AppCompatActivity
import androidx.lifecycle.ViewModelProvider              // ← resuelve ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.datoban.rich_uncle.data.model.Action
import com.datoban.rich_uncle.data.model.ChatMessage
import com.datoban.rich_uncle.data.model.GameState
import com.datoban.rich_uncle.util.Constants
import com.datoban.rich_uncle.visual.ViewModel.GameViewModel
import com.datoban.rich_uncle.visual.ViewModel.GameViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.datoban.rich_uncle.databinding.ActivityGameBinding // ← resuelve R / vistas
import com.datoban.rich_uncle.util.showToast

class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var viewModel: GameViewModel

    private val roomId     by lazy { intent.getStringExtra(Constants.EXTRA_ROOM_ID) ?: "" }
    private val myPlayerId by lazy { FirebaseAuth.getInstance().currentUser?.uid ?: "" }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (roomId.isEmpty()) { finish(); return }

        viewModel = ViewModelProvider(this, GameViewModelFactory())
            .get(GameViewModel::class.java)

        viewModel.loadRoom(roomId)

        setupObservers()
        setupButtons()
        setupChat()
    }

    private fun setupObservers() {
        viewModel.gameState.observe(this)    { state -> updateUI(state) }
        viewModel.messages.observe(this)     { msgs  -> updateChat(msgs) }
        viewModel.eventMessage.observe(this) { event -> if (event.isNotEmpty()) showToast(event) }
        viewModel.actionResult.observe(this) { res   -> binding.tvActionResult.text = res }
        viewModel.gameOver.observe(this)     { result ->
            when (result) {
                "WIN"  -> navigateToResult(won = true)
                "LOSE" -> navigateToResult(won = false)
            }
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener   { viewModel.onActionSelected(roomId, myPlayerId, Action.SAVE)   }
        binding.btnInvest.setOnClickListener { viewModel.onActionSelected(roomId, myPlayerId, Action.INVEST) }
        binding.btnSpend.setOnClickListener  { viewModel.onActionSelected(roomId, myPlayerId, Action.SPEND)  }
        binding.btnRestart.setOnClickListener { viewModel.resetGame(roomId) }
    }

    private fun setupChat() {
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.btnSendChat.setOnClickListener {
            val text = binding.etChatInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val msg = ChatMessage(
                    senderId   = myPlayerId,
                    senderName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Jugador",
                    message    = text,
                    timestamp  = System.currentTimeMillis()
                )
                viewModel.sendChatMessage(roomId, msg)
                binding.etChatInput.text?.clear()
            }
        }
    }

    private fun updateUI(state: GameState) {
        val me = state.room.players[myPlayerId]
        binding.tvTurn.text  = "Turno: ${state.room.currentTurn} / ${state.room.maxTurns}"
        binding.tvMoney.text = "💰 $${me?.money ?: 0}"
    }

    private fun updateChat(msgs: List<ChatMessage>) {
        // binding.rvChat.adapter = ChatAdapter(msgs)  ← conectar tu adapter aquí
        if (msgs.isNotEmpty()) binding.rvChat.scrollToPosition(msgs.size - 1)
    }

    private fun navigateToResult(won: Boolean) {
        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("WON", won)
            putExtra(Constants.EXTRA_ROOM_ID, roomId)
        }
        startActivity(intent)
        finish()
    }
}