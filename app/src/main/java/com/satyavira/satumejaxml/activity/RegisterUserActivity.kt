package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.data_model.InsertUserData
import com.satyavira.satumejaxml.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterUserActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var nisnInputLayout:  EditText
    private lateinit var emailInputLayout: EditText
    private lateinit var nameInputLayout: EditText
    private lateinit var ageInputLayout: EditText
    private lateinit var classesInputLayout: EditText
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register_account)

        dbHelper = DatabaseHelper(this)
        nisnInputLayout = findViewById<EditText>(R.id.edit_text_1)
        emailInputLayout = findViewById<EditText>(R.id.edit_text_2)
        nameInputLayout = findViewById<EditText>(R.id.edit_text_3)
        ageInputLayout = findViewById<EditText>(R.id.edit_text_4)
        classesInputLayout = findViewById<EditText>(R.id.edit_text_6)

        button =
            findViewById(R.id.button)

        val editTexts = arrayOf<EditText>(
            nisnInputLayout,
            emailInputLayout,
            nameInputLayout,
            ageInputLayout,
            classesInputLayout
        )
        val numberEditTexts = arrayOf<EditText>(
            nisnInputLayout,
            ageInputLayout
        )
            nameInputLayout.filters = arrayOf(object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence {
                    val pattern = Regex("[a-zA-Z0-9 ]*")
                    return if (source != null) {
                        if ((pattern.matches(source))) {
                            source
                        } else {
                            nameInputLayout.error = "Please enter a valid ${nameInputLayout.hint}."
                            ""
                        }
                    } else {
                        ""
                    }
                }
            })
        for (editText in numberEditTexts) {
            editText.filters  = arrayOf(object : InputFilter {
                override fun filter(
                    source: CharSequence?,
                    start: Int,
                    end: Int,
                    dest: Spanned?,
                    dstart: Int,
                    dend: Int
                ): CharSequence {
                    val pattern = Regex("[0-9]+")
                    return if (source != null) {
                        if ((pattern.matches(source))) {
                            source
                        } else {
                            nisnInputLayout.error = "Please enter a valid ${editText.hint}."
                            ""
                        }
                    } else {
                        ""
                    }
                }
            })

        }

        classesInputLayout.filters  = arrayOf(object : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence {
                val pattern = Regex("[-(0-9)]+")
                return if (source != null) {
                    if ((pattern.matches(source))) {
                        source
                    } else {
                        classesInputLayout.error = "Please enter a valid ${classesInputLayout.hint}."
                        ""
                    }
                } else {
                    ""
                }
            }
        })


        emailInputLayout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validate email on focus lost
                validateEmail(emailInputLayout)
            }
        }

        classesInputLayout.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { // Validate email on focus lost
                validateClasses(classesInputLayout)
            }
        }

        button.setEnabled(false) // Initially disable the button


        // Text watcher to enable button on all fields filled
        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                val nisn: String = nisnInputLayout.text.toString().trim()
                val email: String = emailInputLayout.text.toString().trim()
                val name: String = nameInputLayout.text.toString().trim()
                val age: String = ageInputLayout.text.toString().trim()
                val classes: String = classesInputLayout.text.toString().trim()

                button.setEnabled(nisn.isNotEmpty() && isValidEmail(email) && name.isNotEmpty() && age.isNotEmpty() && isValidClasses(classes))
            }

            override fun afterTextChanged(editable: Editable) {
            }
        }


        // Add text watcher to all input fields
        for (editText in editTexts) {
            editText.addTextChangedListener(textWatcher)
        }
        button.setOnClickListener {
            navigateToHomepageActivity()
        }

    }
    private fun isValidEmail(email: String): Boolean {
        val pattern = Regex("^[\\w!#$%&'*+/=?^`{|}~-]+(?:\\.[\\w!#$%&'*+/=?^`{|}~-]+)*@(?:\\w(?:[\\w-]*\\w)?\\.)+\\w(?:[\\w-]*\\w)?")
        return pattern.matches(email)
    }
    private fun isValidClasses(classes: String): Boolean {
        val pattern = Regex("[0-9]+(-([0-9])+)*")
        return pattern.matches(classes)
    }
    private fun validateEmail(editText: EditText) {
        val email = editText.text.toString()
        if (!isValidEmail(email)) {
            editText.error = "Please enter a valid email address."
        } else {
            editText.error = null // Remove error if valid
        }
    }
    private fun validateClasses(editText: EditText) {
        val age = editText.text.toString()
        if (!isValidClasses(age)) {
            editText.error = "Please enter a valid class."
        } else {
            editText.error = null // Remove error if valid
        }
    }
    private fun navigateToHomepageActivity() {
        val nisn = findViewById<EditText>(R.id.edit_text_1).text.toString()
        val email = findViewById<EditText>(R.id.edit_text_2).text.toString()
        val name = findViewById<EditText>(R.id.edit_text_3).text.toString()
        val age = findViewById<EditText>(R.id.edit_text_4).text.toString()
        val classes = findViewById<EditText>(R.id.edit_text_6).text.toString()

        val userData = InsertUserData(
            nisn,
            email,
            name,
            age,
            classes
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                dbHelper.insertUserData(userData)
                withContext(Dispatchers.Main) {
                    // Show success message (e.g., Toast)
                    Toast.makeText(this@RegisterUserActivity, "User data saved successfully!", Toast.LENGTH_SHORT).show()
                    Log.d("MyActivity", "Registration Details: NISN: $nisn, Email: $email, Name: $name, Age: $age, Classes: $classes") // Log the details
                    val intent = Intent(this@RegisterUserActivity, HomePageActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message (e.g., Toast)
                    Toast.makeText(this@RegisterUserActivity, "Error saving user data: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.d("MyActivity", "Error saving school data: ${e.message}") // Example logging

                }
            }
        }
    }
}
