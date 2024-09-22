package com.satyavira.satumejaxml.activity

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.adapter.ImageListAdapter
import com.satyavira.satumejaxml.data_model.ImageData
import com.satyavira.satumejaxml.database.DatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmMenuActivity : Activity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imageViewList: RecyclerView
    private lateinit var imageListAdapter: ImageListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

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

        val yes_button = findViewById<Button>(R.id.yes_button)
        yes_button.setOnClickListener {
            navigateToConfirmActivity()
        }
        val no_button = findViewById<Button>(R.id.no_button)
        no_button.setOnClickListener {
            showReasonField()
        }
        val send_button = findViewById<Button>(R.id.send_button)
        send_button.setOnClickListener {
            navigateToConfirm2Activity()
        }
        val home_button = findViewById<ImageButton>(R.id.imageView6)
        home_button.setOnClickListener {
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
    private fun navigateToConfirmActivity() {
        Log.d("MyActivity", "YES") // Example logging
        val intent: Intent =
            Intent(
                this@ConfirmMenuActivity,
                HomePageActivity::class.java
            )
        startActivity(intent)
    }
    private fun navigateToConfirm2Activity() {
        Log.d("MyActivity", "SEND") // Example logging
        val reasonField = findViewById<EditText>(R.id.edit_text_1).text.toString()
        CoroutineScope(Dispatchers.IO).launch {  // Launch on a background thread
            try {
                dbHelper.insertMenuOutside(reasonField) // Insert each image
                 val intent: Intent =
                    Intent(
                        this@ConfirmMenuActivity,
                        HomePageActivity::class.java
                    )
                startActivity(intent)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Show error message (e.g., Toast)
                    Toast.makeText(this@ConfirmMenuActivity, "Error saving menu outside", Toast.LENGTH_SHORT).show()
                    Log.d("MyActivity", "Error saving menu: ${e.message}") // Example logging
                }
            }
        }
        Log.d("MyActivity", "Menu : $reasonField") // Example logging
    }
    private fun showReasonField() {
        Log.d("MyActivity", "NO") // Example logging
        val reasonField = findViewById<EditText>(R.id.edit_text_1)
        val sendButton = findViewById<Button>(R.id.send_button)
        reasonField.visibility = View.VISIBLE
        sendButton.visibility = View.VISIBLE
        val linearLayout = findViewById<LinearLayout>(R.id.line1)
        val yes_button = findViewById<Button>(R.id.yes_button)
        val no_button = findViewById<Button>(R.id.no_button)
        linearLayout.visibility = View.GONE
        yes_button.visibility = View.GONE
        no_button.visibility = View.GONE
    }
    private fun navigateToHomeActivity() {
        Log.d("MyActivity", "Click Home") // Example logging
        val intent: Intent =
            Intent(
                this@ConfirmMenuActivity,
                HomePageActivity::class.java
            )
        startActivity(intent)
    }
}
