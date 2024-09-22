package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.satyavira.satumejaxml.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class BootingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.booting)
        CoroutineScope(Dispatchers.IO).launch {  // Launch on a background thread
            delay(2000L) // Delay for 3 seconds
            val intent = Intent(this@BootingActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}