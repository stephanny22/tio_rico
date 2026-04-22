package com.datoban.rich_uncle.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

object FirebaseService {
    val database by lazy { FirebaseDatabase.getInstance().reference }
    val auth     by lazy { FirebaseAuth.getInstance() }

    fun getRoomRef(roomId: String)    = database.child("rooms").child(roomId)
    fun getPlayersRef(roomId: String) = getRoomRef(roomId).child("players")
    fun getChatRef(roomId: String)    = database.child("chats").child(roomId)
}