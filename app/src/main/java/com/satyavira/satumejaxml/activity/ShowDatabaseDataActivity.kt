package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.adapter.StudentAdapter
import com.satyavira.satumejaxml.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShowDatabaseDataActivity : ComponentActivity() {
    private lateinit var textviewSchoolName: TextView
    private lateinit var textviewSchoolEmail: TextView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var studentViewList: RecyclerView
    private lateinit var studentListAdapter: StudentAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.statistik)
        textviewSchoolName = findViewById(R.id.textview_0)
        textviewSchoolEmail = findViewById(R.id.textview_1)
        studentViewList = findViewById(R.id.siswa_list)
        val homeButton = findViewById<ImageButton>(R.id.imageView6)
        homeButton.setOnClickListener {
            navigateToHomeActivity()
        }
        dbHelper = DatabaseHelper.getInstance(this);

        // Load and display data in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            // Load data from School and UserData tables
            val schools = dbHelper.getAllSchools()
            val userData = dbHelper.getAllUserData()
            for (data in userData) {
                Log.d("MyActivity", "Nama: ${data.name} NISN: ${data.nisn}")
            }

            withContext(Dispatchers.Main) {
                textviewSchoolName.text = schools[0].schoolName;
                textviewSchoolEmail.text = schools[0].email;
                studentListAdapter = StudentAdapter(userData)
                studentViewList.adapter = studentListAdapter
                studentViewList.layoutManager = LinearLayoutManager(this@ShowDatabaseDataActivity, LinearLayoutManager.VERTICAL, false)
            }
        }
    }
    private fun navigateToHomeActivity() {
        Log.d("MyActivity", "Click Home") // Example logging
        val intent: Intent =
            Intent(
                this@ShowDatabaseDataActivity,
                HomePageActivity::class.java
            )
        startActivity(intent)
    }
}
