package com.satyavira.satumejaxml.data_model

data class School(
    val schoolName: String,
    val email: String
)
data class ImageData(
    val id: Int,
    val imageData: ByteArray)
data class MenuOutside(
    val menu: String,
    val date: String)

data class UserData(
    val id: Int,
    val nisn: String,
    val email: String,
    val name: String,
    val age: String,
    val classes: String
)
data class InsertUserData(
    val nisn: String,
    val email: String,
    val name: String,
    val age: String,
    val classes: String
)

data class bahanMakanan(
    val bahan: String,
    val budget: Int
)