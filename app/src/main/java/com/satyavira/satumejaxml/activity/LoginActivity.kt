package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import com.satyavira.satumejaxml.R

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_register)
        val signInButton = findViewById<Button>(R.id.signInButton)
        signInButton.setOnClickListener {
            onSignInButtonClicked()
        }
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            onRegisterButtonClicked()
        }
    }
    private fun onSignInButtonClicked() {
        Log.d("MyActivity", "SIGN IN") // Example logging

        val signInIntent = Intent(this, SignInActivity::class.java)  // Create intent for SignInActivity
        startActivity(signInIntent)                                 // Start SignInActivity
    }

    private fun onRegisterButtonClicked() {
        Log.d("MyActivity", "REGISTER") // Example logging
        val registerIntent = Intent(this, RegisterActivity::class.java) // Create intent for RegisterActivity
        startActivity(registerIntent)                                  // Start RegisterActivity
    }
}
