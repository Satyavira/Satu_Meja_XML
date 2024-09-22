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
import com.satyavira.satumejaxml.data_model.School
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)
        val registerButton = findViewById<Button>(R.id.registerButton)  // Get reference to button
        val schoolNameEditText = findViewById<EditText>(R.id.schoolNameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)

        // Set filters for school name (alphanumeric only)
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

        // Set email validation logic
        emailEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validate email on focus lost
                validateEmail(emailEditText)
            }
        }
        schoolNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validate email on focus lost
                if (!isValidSchoolName(schoolNameEditText.text.toString())) {
                    schoolNameEditText.error = "Please enter a valid school name."
                    schoolNameEditText.setText("")
                }
            }
        }
        registerButton.setOnClickListener { // Alternatively, you can use the onClick attribute
            onRegisterButtonClicked()
        }
    }
    private fun isValidSchoolName(schoolName: String): Boolean {
        val pattern = Regex("[A-Z][a-zA-Z0-9]*((\\s[A-Z][a-zA-Z0-9]*)|\\s[0-9][a-zA-Z0-9]*)*") // Allow alphanumeric characters only
        return pattern.matches(schoolName)
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Regex("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:\\w(?:[\\w-]*\\w)?\\.)+\\w(?:[\\w-]*\\w)?")
        return pattern.matches(email)
    }

    private fun validateEmail(editText: EditText) {
        val email = editText.text.toString()
        if (!isValidEmail(email)) {
            editText.error = "Please enter a valid email address."
        } else {
            editText.error = null // Remove error if valid
        }
    }
    private fun onRegisterButtonClicked() {
        val schoolName = findViewById<EditText>(R.id.schoolNameEditText).text.toString()  // Get school name
        val email = findViewById<EditText>(R.id.emailEditText).text.toString()      // Get email

        // Get an instance of the UserDao
        val database = DatabaseHelper.getInstance(this)
        val school = School(
            schoolName,
            email
            )
        // Insert data in a background thread
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Validate school name and email before proceeding
                if (isValidSchoolName(schoolName) && isValidEmail(email)) {
                    if (!database.doesSchoolExist(school.schoolName)) {
                        database.insertSchool(school)
                        withContext(Dispatchers.Main) {
                            // Show success message (e.g., Toast)
                            Toast.makeText(this@RegisterActivity, "School data saved successfully!", Toast.LENGTH_SHORT).show()
                            Log.d("MyActivity", "Saved school data: School: " + school.schoolName + ", Email: " + school.email)
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            // Show success message (e.g., Toast)
                            findViewById<EditText>(R.id.emailEditText).setText("")
                            findViewById<EditText>(R.id.schoolNameEditText).setText("")
                            Toast.makeText(this@RegisterActivity, "School already exist!", Toast.LENGTH_SHORT).show()
                            Log.d("MyActivity", "Saved already exist -> School: " + school.schoolName)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                    // Show error messages if validation fails
                        Toast.makeText(this@RegisterActivity, "Invalid school name or email format!", Toast.LENGTH_SHORT).show()
                        Log.d("MyActivity", "Saved school data: School: " + school.schoolName + ", Email: " + school.email)
                    }
                }
//                For cleaning the the school table
//                for (allSchool in database.getAllSchools()) {
//                    Log.d("MyActivity", "School Name: " + allSchool.schoolName + " Email: " + allSchool.email)
//                    database.deleteSchool(allSchool.schoolName)
//                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message (e.g., Toast)
                    Toast.makeText(this@RegisterActivity, "Error saving school data: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("MyActivity", "Error saving school data: ${e.message}") // Example logging
                }
            }
        }
    }
}
