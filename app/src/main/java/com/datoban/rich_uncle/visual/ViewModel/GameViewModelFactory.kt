package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.datoban.rich_uncle.data.repository.ChatRepository
import com.datoban.rich_uncle.data.repository.GameRepository
import com.datoban.rich_uncle.domain.usecase.ApplyRandomEventUseCase
import com.datoban.rich_uncle.domain.usecase.CheckVictoryUseCase
import com.datoban.rich_uncle.domain.usecase.PerformActionUseCase

class GameViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(
                GameRepository(),
                ChatRepository(),
                PerformActionUseCase(),
                ApplyRandomEventUseCase(),
                CheckVictoryUseCase()
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}