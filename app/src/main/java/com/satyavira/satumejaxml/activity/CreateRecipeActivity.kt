package com.satyavira.satumejaxml.activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.adapter.ImageBitmapAdapter
import com.satyavira.satumejaxml.database.DatabaseHelper
import com.satyavira.satumejaxml.data_model.bahanMakanan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CreateRecipeActivity : Activity() {
    private lateinit var captureImageButton: Button
    private lateinit var imageContainer: RecyclerView
    private lateinit var imageBitmapAdapter: ImageBitmapAdapter
    private val images: ArrayList<Bitmap> = ArrayList() // List to hold captured images
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sendButton: Button
    private lateinit var addButton: Button
    private lateinit var tableLayout: TableLayout
    private lateinit var bahanEditText: EditText
    private lateinit var budgetEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chef)

        addButton = findViewById(R.id.add_button)
        tableLayout = findViewById(R.id.tabel_bahan)
        bahanEditText = findViewById(R.id.edit_text_2)
        budgetEditText = findViewById(R.id.edit_text_1)

        addButton.setOnClickListener {
            handleClickAdd()
        }

        captureImageButton = findViewById(R.id.capture_image_button)

        imageContainer = findViewById(R.id.image_list)
        imageBitmapAdapter = ImageBitmapAdapter(images)
        imageContainer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        imageContainer.visibility = View.VISIBLE
        imageContainer.adapter = imageBitmapAdapter

        captureImageButton.setOnClickListener {
            openCamera()
        }

        sendButton = findViewById(R.id.button)
        updateSendButtonEnabledState()
        sendButton.setOnClickListener { // Navigate to VerifyFingerprintActivity
            handleClick()
        }

        val homeButton = findViewById<ImageButton>(R.id.imageView6)
        homeButton.setOnClickListener {
            navigateToHomeActivity()
        }
    }
    private fun saveTableDataToDatabase() {
        dbHelper = DatabaseHelper(this)

        val rowCount = tableLayout.childCount
        for (i in 1 until rowCount) { // Skip the header row
            val row = tableLayout.getChildAt(i) as TableRow
            val bahanTextView = row.getChildAt(0) as TextView
            val budgetTextView = row.getChildAt(1) as TextView

            val bahan = bahanTextView.text.toString().trim { it <= ' ' }
            val budget = budgetTextView.text.toString().trim { it <= ' ' }
            // ... (rest of your data retrieval code)
            val bahanMakanan = bahanMakanan(bahan, budget.toInt())
            CoroutineScope(Dispatchers.IO).launch {  // Launch on a background thread
                try {
                    dbHelper.insertRecipe(bahanMakanan)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Show error message (e.g., Toast)
                        Toast.makeText(this@CreateRecipeActivity, "Error saving images ${i}: ${e.message}", Toast.LENGTH_SHORT).show()
                        Log.d("MyActivity", "Error saving images: ${e.message}") // Example logging
                    }
                }
            }
        }
    }

    private fun handleClickAdd() {
        val bahan = bahanEditText.text.toString().trim { it <= ' ' } // Get and trim user input
        val budget = budgetEditText.text.toString().trim { it <= ' ' }


        // Error handling (optional): Check if input is empty or invalid
        if (bahan.isEmpty() || budget.isEmpty()) {
            Toast.makeText(
                this@CreateRecipeActivity,
                "Please enter both bahan and budget",
                Toast.LENGTH_SHORT
            ).show()
            Log.d(
                "MyActivity",
                "Please enter both bahan and budget"
            )
            return
        }


        // Create a new TableRow dynamically
        val newRow = TableRow(this@CreateRecipeActivity)


        // Create TextViews for bahan and budget
        val bahanTextView = TextView(this@CreateRecipeActivity)
        bahanTextView.text = bahan
        bahanTextView.gravity = Gravity.CENTER
        bahanTextView.setPadding(8, 8, 8, 8)
        bahanTextView.layoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)

        val budgetTextView = TextView(this@CreateRecipeActivity)
        budgetTextView.text = budget
        budgetTextView.gravity = Gravity.CENTER
        budgetTextView.setPadding(8, 8, 8, 8)
        budgetTextView.layoutParams =
            TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT   , TableRow.LayoutParams.WRAP_CONTENT, 1f)


        // Add a divider between them
        val divider = View(this@CreateRecipeActivity)
        divider.setBackgroundColor(Color.BLACK)
        divider.layoutParams =
            TableRow.LayoutParams(1, LayoutParams.MATCH_PARENT)


        // Add elements to the new row
        newRow.addView(bahanTextView)
        newRow.addView(divider)
        newRow.addView(budgetTextView)


        // Add the new row to the table
        tableLayout.addView(newRow)


        // Clear the input fields for next entry
        bahanEditText.setText("")
        budgetEditText.setText("")
        updateSendButtonEnabledState()
    }

    private fun updateSendButtonEnabledState() {
        if (images.size > 0 && tableLayout.childCount > 1) {
            sendButton.visibility = View.VISIBLE
        } else {
            sendButton.visibility = View.GONE
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(
            intent,
            CAMERA_REQUEST
        ) // Request code for capturing image
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            // Image captured successfully
            val extras = data.extras
            val imageBitmap = extras!!["data"] as Bitmap

            images.add(imageBitmap)
            imageBitmapAdapter.notifyItemInserted(images.size - 1)  // Notify adapter of new item
            updateSendButtonEnabledState()
        }
    }
    private fun navigateToHomeActivity() {
        Log.d("MyActivity", "Click Home") // Example logging
        val intent: Intent =
            Intent(
                this@CreateRecipeActivity,
                HomePageActivity::class.java
            )
        startActivity(intent)
    }
    private fun handleClick() {
        insertAllImagesToDatabase()
        saveTableDataToDatabase()

        Log.d("MyActivity", "Go To Homepage") // Example logging
        val intent: Intent = Intent(this@CreateRecipeActivity, HomePageActivity::class.java)
        startActivity(intent)
    }
    private fun insertAllImagesToDatabase() {
        if (imageBitmapAdapter.itemCount > 0) {
            for (i in 0 until imageBitmapAdapter.itemCount) {
                val image: Bitmap = imageBitmapAdapter.images[i]
                val databaseHelper = DatabaseHelper.getInstance(this)

                CoroutineScope(Dispatchers.IO).launch {  // Launch on a background thread
                    try {
                        databaseHelper.insertImage(image) // Insert each image
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            // Show error message (e.g., Toast)
                            Toast.makeText(this@CreateRecipeActivity, "Error saving images ${i}: ${e.message}", Toast.LENGTH_SHORT).show()
                            Log.d("MyActivity", "Error saving images: ${e.message}") // Example logging
                        }
                    }
                }
            }
        }
    }
    companion object {
        private const val CAMERA_REQUEST = 1888
    }
}
