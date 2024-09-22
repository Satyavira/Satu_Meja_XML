package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        val schoolNameEditText = findViewById<EditText>(R.id.schoolNameEditText)
        schoolNameEditText.filters = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                val pattern = Regex("[a-zA-Z0-9\\s]*")
                return if (source != null) {
                    if ((pattern.matches(source))) {
                        source
                    } else {
                        schoolNameEditText.error = "Please enter a valid school name."
                        ""
                    }
                } else {
                    ""
                }
            }
        })
        schoolNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validate email on focus lost
                if (!isValidSchoolName(schoolNameEditText.text.toString())) {
                    schoolNameEditText.error = "Please enter a valid school name."
                    schoolNameEditText.setText("")
                }
            }
        }
        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener {
            onLoginButtonClicked()
        }
    }
    private fun isValidSchoolName(schoolName: String): Boolean {
        val pattern = Regex("[A-Z][a-zA-Z0-9]*((\\s[A-Z][a-zA-Z0-9]*)|\\s[0-9][a-zA-Z0-9]*)*") // Allow alphanumeric characters only
        return pattern.matches(schoolName)
    }
    private fun onLoginButtonClicked() {
        val schoolName =
            this.findViewById<EditText>(R.id.schoolNameEditText).text.toString()  // Get school name
        val database = DatabaseHelper.getInstance(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Validate school name and email before proceeding
                if (isValidSchoolName(schoolName)) {
                    if (database.doesSchoolExist(schoolName)) {
                        withContext(Dispatchers.Main) {
                            // Show success message (e.g., Toast)
                            Toast.makeText(
                                this@SignInActivity,
                                "You have login successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                "MyActivity",
                                "Login school data: School: $schoolName"
                            )
                            val intent = Intent(this@SignInActivity, HomePageActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            findViewById<EditText>(R.id.schoolNameEditText).setText("")
                            // Show error messages if validation fails
                            Toast.makeText(
                                this@SignInActivity,
                                "School not found!",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d(
                                "MyActivity",
                                "Invalid school name, School name not found!  -> $schoolName"
                            )
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        findViewById<EditText>(R.id.schoolNameEditText).setText("")
                        // Show error messages if validation fails
                        Toast.makeText(
                            this@SignInActivity,
                            "Invalid school name!",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(
                            "MyActivity",
                            "Invalid school name!  -> $schoolName"
                        )
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message (e.g., Toast)
                    Toast.makeText(
                        this@SignInActivity,
                        "Error checking school data: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("MyActivity", "Error checking school data: ${e.message}") // Example logging
                }
            }
        }
    }
}
