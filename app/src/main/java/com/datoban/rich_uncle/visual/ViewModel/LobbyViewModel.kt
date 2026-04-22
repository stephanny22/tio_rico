package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.datoban.rich_uncle.data.model.GameRoom
import com.datoban.rich_uncle.data.model.Player
import com.datoban.rich_uncle.data.remote.FirebaseService
import com.datoban.rich_uncle.data.repository.GameRepository
import com.datoban.rich_uncle.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LobbyViewModel(private val repo: GameRepository) : ViewModel() {

    private val _room = MutableLiveData<GameRoom>()
    val room: LiveData<GameRoom> = _room

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Guardamos referencia al listener para poder quitarlo si hace falta
    private var roomListener: ValueEventListener? = null
    private var currentRoomId: String = ""

    fun createRoom(roomId: String) {
        val newRoom = GameRoom(
            roomId   = roomId,
            maxTurns = Constants.MAX_TURNS,
            status   = "WAITING"
        )
        FirebaseService.getRoomRef(roomId)
            .setValue(newRoom)
            .addOnSuccessListener {
                observeRoom(roomId)   // ← empezar a escuchar DESPUÉS de crear
            }
            .addOnFailureListener { e ->
                _error.value = "Error creando sala: ${e.message}"
            }
    }

    fun joinRoom(roomId: String, player: Player) {
        // Primero verificar que la sala existe
        FirebaseService.getRoomRef(roomId).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.exists()) {
                    _error.value = "La sala '$roomId' no existe"
                    return@addOnSuccessListener
                }
                // Agregar jugador y luego escuchar
                FirebaseService.getPlayersRef(roomId)
                    .child(player.id)
                    .setValue(player)
                    .addOnSuccessListener {
                        observeRoom(roomId)   // ← escuchar DESPUÉS de unirse
                    }
                    .addOnFailureListener { e ->
                        _error.value = "Error uniéndose: ${e.message}"
                    }
            }
            .addOnFailureListener { e ->
                _error.value = "Error verificando sala: ${e.message}"
            }
    }
    fun getCurrentUserData(onResult: (Player) -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)
            .get()
            .addOnSuccessListener { snapshot ->

                val name = snapshot.child("name").getValue(String::class.java) ?: "Jugador"

                val player = Player(
                    id = uid,
                    name = name,
                    money = Constants.INITIAL_MONEY,
                    alive = true,
                    lastAction = "",
                    lastResult = 0
                )

                onResult(player)
            }
    }
    fun startGame(roomId: String) {
        if (roomId.isEmpty()) {
            _error.value = "Ingresa un ID de sala primero"
            return
        }
        repo.setRoomStatus(roomId, "PLAYING")
    }

    private fun observeRoom(roomId: String) {
        // Evitar listeners duplicados
        if (currentRoomId == roomId && roomListener != null) return

        currentRoomId = roomId

        roomListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val id          = snapshot.child("roomId").getValue(String::class.java) ?: roomId
                    val currentTurn = snapshot.child("currentTurn").getValue(Int::class.java) ?: 0
                    val maxTurns    = snapshot.child("maxTurns").getValue(Int::class.java) ?: 10
                    val status      = snapshot.child("status").getValue(String::class.java) ?: "WAITING"

                    val players = mutableMapOf<String, Player>()
                    snapshot.child("players").children.forEach { playerSnap ->
                        val p = playerSnap.getValue(Player::class.java)
                        if (p != null) players[playerSnap.key ?: ""] = p
                    }

                    _room.value = GameRoom(
                        roomId      = id,
                        players     = players,
                        currentTurn = currentTurn,
                        maxTurns    = maxTurns,
                        status      = status
                    )
                } catch (e: Exception) {
                    _error.value = "Error leyendo sala: ${e.message}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _error.value = "Firebase cancelado: ${error.message}"
            }
        }

        FirebaseService.getRoomRef(roomId).addValueEventListener(roomListener!!)
    }

    override fun onCleared() {
        super.onCleared()
        // Limpiar listener al destruir el ViewModel
        if (currentRoomId.isNotEmpty() && roomListener != null) {
            FirebaseService.getRoomRef(currentRoomId).removeEventListener(roomListener!!)
        }
    }
    fun register(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val auth = FirebaseAuth.getInstance()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user?.uid ?: return@addOnSuccessListener

                val userMap = mapOf(
                    "id" to uid,
                    "name" to name,
                    "email" to email
                )

                FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(uid)
                    .setValue(userMap)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Error guardando usuario")
                    }
            }
            .addOnFailureListener {
                onError(it.message ?: "Error en registro")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError(it.message ?: "Error al iniciar sesión")
            }
    }
}