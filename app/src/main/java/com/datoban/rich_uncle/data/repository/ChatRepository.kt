package com.datoban.rich_uncle.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.datoban.rich_uncle.data.model.ChatMessage
import com.datoban.rich_uncle.data.remote.FirebaseService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ChatRepository {

    fun observeMessages(roomId: String): LiveData<List<ChatMessage>> {
        val liveData = MutableLiveData<List<ChatMessage>>()
        FirebaseService.getChatRef(roomId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull {
                        it.getValue(ChatMessage::class.java)
                    }
                    liveData.value = messages
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        return liveData
    }

    fun sendMessage(roomId: String, message: ChatMessage) {
        FirebaseService.getChatRef(roomId)
            .push()
            .setValue(message)
    }
}