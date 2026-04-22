package com.datoban.rich_uncle

import android.os.Bundle
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.datoban.rich_uncle.visual.activity.LobbyActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirige directo al Lobby
        startActivity(Intent(this, LobbyActivity::class.java))
        finish()
    }
}