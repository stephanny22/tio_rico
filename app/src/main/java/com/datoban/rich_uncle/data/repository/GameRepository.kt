package com.datoban.rich_uncle.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.datoban.rich_uncle.data.model.GameRoom
import com.datoban.rich_uncle.data.model.Player
import com.datoban.rich_uncle.data.remote.FirebaseService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class GameRepository {

    fun observeRoom(roomId: String): LiveData<GameRoom> {
        val liveData = MutableLiveData<GameRoom>()

        FirebaseService.getRoomRef(roomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val roomId   = snapshot.child("roomId").getValue(String::class.java) ?: ""
                        val currentTurn = snapshot.child("currentTurn").getValue(Int::class.java) ?: 0
                        val maxTurns = snapshot.child("maxTurns").getValue(Int::class.java) ?: 10
                        val status   = snapshot.child("status").getValue(String::class.java) ?: "WAITING"

                        // Deserializar jugadores manualmente
                        val players = mutableMapOf<String, Player>()

                        snapshot.child("players").children.forEach { playerSnap ->

                            val id = playerSnap.child("id").getValue(String::class.java) ?: ""
                            val name = playerSnap.child("name").getValue(String::class.java) ?: "Jugador"
                            val money = playerSnap.child("money").getValue(Int::class.java) ?: 0
                            val alive = playerSnap.child("alive").getValue(Boolean::class.java) ?: true
                            val lastAction = playerSnap.child("lastAction").getValue(String::class.java) ?: ""
                            val lastResult = playerSnap.child("lastResult").getValue(Int::class.java) ?: 0

                            val player = Player(
                                id = id,
                                name = name,
                                money = money,
                                alive = alive,
                                lastAction = lastAction,
                                lastResult = lastResult
                            )

                            players[playerSnap.key ?: ""] = player
                        }

                        liveData.value = GameRoom(
                            roomId      = roomId,
                            players     = players,
                            currentTurn = currentTurn,
                            maxTurns    = maxTurns,
                            status      = status
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("GameRepository", "Error deserializando sala: ${e.message}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    android.util.Log.e("GameRepository", "Firebase cancelado: ${error.message}")
                }
            })

        return liveData
    }

    fun updatePlayer(roomId: String, player: Player) {
        FirebaseService.getPlayersRef(roomId)
            .child(player.id)
            .setValue(player)
    }

    fun advanceTurn(roomId: String, nextTurn: Int) {
        FirebaseService.getRoomRef(roomId)
            .child("currentTurn")
            .setValue(nextTurn)
    }

    fun setRoomStatus(roomId: String, status: String) {
        FirebaseService.getRoomRef(roomId)
            .child("status")
            .setValue(status)
    }

    fun resetRoom(roomId: String) {
        FirebaseService.getRoomRef(roomId).get()
            .addOnSuccessListener { snapshot ->

                val playersSnap = snapshot.child("players")

                playersSnap.children.forEach { playerSnap ->

                    val id = playerSnap.child("id").getValue(String::class.java) ?: return@forEach
                    val name = playerSnap.child("name").getValue(String::class.java) ?: "Jugador"

                    val player = Player(
                        id = id,
                        name = name,
                        money = 1000,
                        alive = true,
                        lastAction = "",
                        lastResult = 0
                    )

                    updatePlayer(roomId, player)
                }

                advanceTurn(roomId, 0)
                setRoomStatus(roomId, "WAITING")
            }
    }
}