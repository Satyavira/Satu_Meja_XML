package com.satyavira.satumejaxml.activity

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.adapter.ImageListAdapter
import com.satyavira.satumejaxml.data_model.ImageData
import com.satyavira.satumejaxml.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePageActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imageViewList: RecyclerView
    private lateinit var imageListAdapter: ImageListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homepage)
        imageViewList = findViewById(R.id.image_list)
        dbHelper = DatabaseHelper(this)
        imageListAdapter = ImageListAdapter() // Initially empty list

        // Get images for today (or any query you need)
        val cursor = dbHelper.getImagesForToday()

        // Process data and update adapter (combined into one step)
        val imageDataList = processAndConvertCursorToList(cursor)
        imageListAdapter.imageData = imageDataList
        imageListAdapter.notifyDataSetChanged()
        imageViewList.adapter = imageListAdapter
        imageViewList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageViewList.visibility = View.VISIBLE
        val headmasterButton = findViewById<Button>(R.id.button1)
        headmasterButton.setOnClickListener {
            navigateToHeadmasterActivity()
        }
        val resepButton = findViewById<Button>(R.id.button2)
        resepButton.setOnClickListener {
            navigateToChefActivity()
        }
        val cekMenuButton = findViewById<Button>(R.id.button3)
        cekMenuButton.setOnClickListener {
            navigateToMenuActivity()
        }
        val homeButton = findViewById<ImageButton>(R.id.imageView4)
        homeButton.setOnClickListener {
            navigateToHomeActivity()
        }
    }
    private fun processAndConvertCursorToList(cursor: Cursor?): List<ImageData> {
        val imageList = mutableListOf<ImageData>()
        if (cursor != null && cursor.moveToFirst()) {
            val idColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)
            val imageDataColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_DATA)

            do {
                val id = cursor.getInt(idColumnIndex)
                val imageData = cursor.getBlob(imageDataColumnIndex)
                imageList.add(ImageData(id, imageData)) // Add data to list
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return imageList
    }
    private fun navigateToChefActivity() {
        Log.d("MyActivity", "Chef") // Example logging
        val intent: Intent = Intent(this@HomePageActivity, CreateRecipeActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToMenuActivity() {
        Log.d("MyActivity", "Student") // Example logging
        val intent: Intent = Intent(this@HomePageActivity, ConfirmMenuActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToHeadmasterActivity() {
        Log.d("MyActivity", "Headmaster") // Example logging
        val intent: Intent = Intent(this@HomePageActivity, HeadmasterActivity::class.java)
        startActivity(intent)
    }
    private fun navigateToHomeActivity() {
        Log.d("MyActivity", "Click Home") // Example logging
        CoroutineScope(Dispatchers.IO).launch {  // Launch on a background thread
                delay(1000L)
                val intent: Intent =
                    Intent(
                        this@HomePageActivity,
                        HomePageActivity::class.java
                    )
                startActivity(intent)
                finish()
        }
    }
}
