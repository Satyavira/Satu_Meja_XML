package com.satyavira.satumejaxml.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import com.satyavira.satumejaxml.data_model.InsertUserData
import com.satyavira.satumejaxml.data_model.MenuOutside
import com.satyavira.satumejaxml.data_model.School
import com.satyavira.satumejaxml.data_model.UserData
import com.satyavira.satumejaxml.data_model.bahanMakanan
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "satumeja.db"
        private const val DATABASE_VERSION = 1
        private var instance: DatabaseHelper? = null
        private const val IMAGE_TABLE_NAME = "images"
        private const val COLUMN_BAHAN = "bahan"
        private const val COLUMN_BUDGET = "budget"
        private const val BAHAN_MAKANAN_TABLE_NAME = "bahan_makanan"
        const val COLUMN_ID = "id"
        const val COLUMN_IMAGE_DATA = "image_data"

        fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables
        db.execSQL("CREATE TABLE school (" +
                "nama_sekolah TEXT NOT NULL PRIMARY KEY," +
                "email TEXT NOT NULL)")

        db.execSQL("CREATE TABLE user_data (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nisn TEXT NOT NULL," +
                "email TEXT NOT NULL," +
                "name TEXT NOT NULL," +
                "age INTEGER," +
                "classes TEXT NOT NULL)")
        db.execSQL("CREATE TABLE " + IMAGE_TABLE_NAME + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_IMAGE_DATA + " BLOB NOT NULL, " +
        "date_added DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");
        db.execSQL("CREATE TABLE " + BAHAN_MAKANAN_TABLE_NAME + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BAHAN + " BLOB NOT NULL, " +
                COLUMN_BUDGET + " TEXT NOT NULL, " +
        "date_added DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");
        db.execSQL("CREATE TABLE " + "menu_diluar_yang_seharusnya" + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "menu" + " TEXT NOT NULL, " +
        "date_added DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL)");
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database schema changes if needed
        // (Example: Drop tables and recreate them in a new version)
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS school");
        db.execSQL("DROP TABLE IF EXISTS user_data");
        db.execSQL("DROP TABLE IF EXISTS images")
        db.execSQL("DROP TABLE IF EXISTS $BAHAN_MAKANAN_TABLE_NAME")

        // Create tables again
        onCreate(db);
    }
    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream)
        return outputStream.toByteArray()
    }
    fun getImagesByDate(targetDate: String): Cursor {
        val db = readableDatabase
        val selection = "strftime('%Y-%m-%d', date_added) = ?" // Extract only date part
        val selectionArgs = arrayOf(targetDate)
        val cursor: Cursor = db.query(
            IMAGE_TABLE_NAME,
            null,  // Select all columns
            selection,
            selectionArgs,
            null,  // No group by
            null,  // No having
            null // No order by
        )
        return cursor
    }
    fun getImagesForToday(): Cursor {
        val db = readableDatabase
        val selection = "date_added >= ? AND date_added < ?" // Filter for today
        val selectionArgs = arrayOfNulls<String>(2)
        selectionArgs[0] = getTodayDateString() // Start of today
        selectionArgs[1] = getTomorrowDateString() // Start of tomorrow
        val cursor: Cursor = db.query(
            IMAGE_TABLE_NAME,
            null,  // Select all columns
            selection,
            selectionArgs,
            null,  // No group by
            null,  // No having
            null // No order by
        )
        return cursor
    }

    private fun getTodayDateString(): String {
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val today: Date = Date()
        return sdf.format(today)
    }

    private fun getTomorrowDateString(): String {
        val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 1) // Add 1 day
        val tomorrow: Date = calendar.getTime()
        return sdf.format(tomorrow)
    }

    fun doesSchoolExist(schoolName: String): Boolean {
        val db = readableDatabase
        val cursor = db.query(
            "school", // Table name
            null, // Select all columns
            "nama_sekolah = ?", // Selection criteria (school name)
            arrayOf(schoolName), // Selection arguments (school name value)
            null, // Group by
            null, // Having
            null // Order by
        )

        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun insertRecipe(bahanMakanan: bahanMakanan) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_BAHAN, bahanMakanan.bahan)
            put(COLUMN_BUDGET, bahanMakanan.budget)
        }
        db.insert(BAHAN_MAKANAN_TABLE_NAME, null, values)
        db.close()
    }
    fun insertImage(image: Bitmap) {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COLUMN_IMAGE_DATA, getBitmapAsByteArray(image))
        db.insert(IMAGE_TABLE_NAME, null, values)
        db.close()
    }
    fun insertMenuOutside(menu: String) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("menu", menu)
        db.insert("menu_diluar_yang_seharusnya", null, values)
        db.close()
    }
    fun insertSchool(school: School) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("nama_sekolah", school.schoolName)
            put("email", school.email)
        }
        db.insert("school", null, values)
        db.close()
    }

    fun insertUserData(userData: InsertUserData) {
        val db = writableDatabase
        val cursor = db.query("school", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            cursor.getString(0)
            break;
        }
        cursor.close()
        val values = ContentValues().apply {
            put("nisn", userData.nisn)
            put("email", userData.email)
            put("name", userData.name)
            put("age", userData.age)
            put("classes", userData.classes)
        }
        db.insert("user_data", null, values)
        db.close()
    }

    fun getAllSchools(): List<School> {
        val schools = mutableListOf<School>()
        val db = readableDatabase
        val cursor = db.query("school", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val school = School(cursor.getString(0), cursor.getString(1))
            schools.add(school)
        }
        cursor.close()
        return schools
    }
    fun getAllMenuOutside(): List<MenuOutside> {
        val menuOutsideList = mutableListOf<MenuOutside>()
        val db = readableDatabase
        val cursor = db.query("menu_diluar_yang_seharusnya", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val menuOutside = MenuOutside(cursor.getString(1), cursor.getString(2))
            menuOutsideList.add(menuOutside)
        }
        cursor.close()
        return menuOutsideList
    }
    fun getAllUserData(): List<UserData> {
        val userDataList = mutableListOf<UserData>()
        val db = readableDatabase
        val cursor = db.query("user_data", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            val userData = UserData(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
                cursor.getString(5)
            )
            userDataList.add(userData)
        }
        cursor.close()
        return userDataList
    }
    fun getAllImages(): List<String> {
        val imagesList = mutableListOf<String>()
        val db = readableDatabase
        val cursor = db.query("user_data", null, null, null, null, null, null)
        while (cursor.moveToNext()) {
            imagesList.add(cursor.getString(0))
        }
        cursor.close()
        return imagesList
    }
    fun deleteSchool(name: String) {
        val db = writableDatabase
        db.delete("school", "nama_sekolah = ?", arrayOf(name))
        db.close()
    }
    fun deleteUserData(name: String) {
        val db = writableDatabase
        db.delete("user_data", "name = ?", arrayOf(name))
        db.close()
    }
    fun deleteAllUserData(name: List<UserData>) {
        val db = writableDatabase
        db.delete("user_data", "name = ?", name.stream().toArray() as Array<out String>?)
        db.close()
    }
    fun deleteImages(name: String) {
        val db = writableDatabase
        db.delete("images", "id = ?", arrayOf(name))
        db.close()
    }
    fun deleteAllImages() {
        val db = writableDatabase
        val ids = getAllImages()
        for (id in ids) {
            db.delete("images", "id = ?" , arrayOf(id))
        }
        db.close()
    }
    fun updateSchoolEmail(oldEmail: String, newEmail: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", newEmail)
        }
        db.update("school", values, "email = ?", arrayOf(oldEmail))
        db.close()
    }
}
