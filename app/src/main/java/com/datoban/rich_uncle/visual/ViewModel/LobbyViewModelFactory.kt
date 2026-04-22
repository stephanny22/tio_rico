package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datoban.rich_uncle.data.repository.GameRepository

class LobbyViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LobbyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LobbyViewModel(GameRepository()) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}