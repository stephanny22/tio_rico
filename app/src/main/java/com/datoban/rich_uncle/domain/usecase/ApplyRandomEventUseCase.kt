package com.datoban.rich_uncle.domain.usecase

import com.datoban.rich_uncle.util.Constants
import kotlin.random.Random

class ApplyRandomEventUseCase {
    fun execute(): Pair<String, Int> {
        if (Random.nextFloat() > Constants.EVENT_CHANCE) {
            return Pair("", 0) // sin evento
        }
        val events = listOf(
            Pair("🎉 ¡Bono inesperado!",       +150),
            Pair("🔧 Reparación urgente.",      -100),
            Pair("📈 Inversión extra.",          +80),
            Pair("🏥 Gasto médico inesperado.", -120)
        )
        return events.random()
    }
}