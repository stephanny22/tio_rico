package com.datoban.rich_uncle

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.datoban.rich_uncle.visual.activity.LobbyActivity
import com.datoban.rich_uncle.visual.activity.LoginActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}