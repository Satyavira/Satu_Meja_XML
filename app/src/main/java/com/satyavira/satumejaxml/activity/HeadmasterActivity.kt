package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import com.satyavira.satumejaxml.R

class HeadmasterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.headmaster)
        val statisticButton = findViewById<Button>(R.id.button1)
        statisticButton.setOnClickListener {
            navigateToStatisticActivity()
        }
        val registerUserButton = findViewById<Button>(R.id.button2)
        registerUserButton.setOnClickListener {
            navigateToRegisterUserActivity()
        }
        val home_button = findViewById<ImageButton>(R.id.imageView6)
        home_button.setOnClickListener {
            navigateToHomeActivity()
        }
    }
    private fun navigateToStatisticActivity() {
        Log.d("MyActivity", "Statistic") // Example logging
        val intent: Intent = Intent(this@HeadmasterActivity, ShowDatabaseDataActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToRegisterUserActivity() {
        Log.d("MyActivity", "Student") // Example logging
        val intent: Intent = Intent(this@HeadmasterActivity, RegisterUserActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToHomeActivity() {
        Log.d("MyActivity", "Click Home") // Example logging
        val intent: Intent =
            Intent(
                this@HeadmasterActivity,
                HomePageActivity::class.java
            )
        startActivity(intent)
    }
}
