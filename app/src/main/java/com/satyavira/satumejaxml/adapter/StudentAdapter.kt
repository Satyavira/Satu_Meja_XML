package com.satyavira.satumejaxml.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.satyavira.satumejaxml.R
import com.satyavira.satumejaxml.data_model.UserData
import kotlin.Int


class StudentAdapter(private val students: List<UserData>) :
    RecyclerView.Adapter<StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.student_list_item, parent, false)
        return StudentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student: UserData = students[position]
        holder.idTextView.text = student.id.toString()
        holder.nameTextView.text = student.name
        holder.nisnTextView.text = student.nisn
    }

    override fun getItemCount(): Int {
        return students.size
    }
}

class StudentViewHolder(itemView: View) : ViewHolder(itemView) {
    val idTextView: TextView = itemView.findViewById<TextView>(R.id.user_item_id)
    val nameTextView: TextView = itemView.findViewById<TextView>(R.id.user_item_name)
    val nisnTextView: TextView = itemView.findViewById<TextView>(R.id.user_item_nisn)
}
