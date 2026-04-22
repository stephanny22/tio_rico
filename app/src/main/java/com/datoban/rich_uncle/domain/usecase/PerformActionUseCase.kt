package com.datoban.rich_uncle.domain.usecase

import com.datoban.rich_uncle.data.model.Action
import com.datoban.rich_uncle.data.repository.GameRepository
import kotlin.random.Random

class PerformActionUseCase {
    fun execute(action: Action): Pair<Int, String> {
        return when (action) {
            Action.SAVE   -> Pair(+100, "Ahorraste con éxito. +$100")
            Action.INVEST -> if (Random.nextBoolean())
                Pair(+200, "¡Inversión exitosa! +$200")
            else
                Pair(-150, "Mala inversión. -$150")
            Action.SPEND  -> Pair(-80, "Gastaste dinero. -$80")
        }
    }
}