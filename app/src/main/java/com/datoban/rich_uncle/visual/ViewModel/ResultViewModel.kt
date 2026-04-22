package com.datoban.rich_uncle.visual.ViewModel

import androidx.lifecycle.ViewModel
import com.datoban.rich_uncle.data.model.Player

class ResultViewModel : ViewModel() {
    fun getWinners(players: List<Player>): List<Player> =
        players.filter { it.isAlive }

    fun getLosers(players: List<Player>): List<Player> =
        players.filter { !it.isAlive }
}