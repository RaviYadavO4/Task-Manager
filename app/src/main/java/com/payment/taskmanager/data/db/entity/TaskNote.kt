package com.payment.taskmanager.data.db.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "task_note")
data class TaskNote(
    @PrimaryKey(autoGenerate = true)
     val uniqueSlug: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "due_date") val dueDate: String,
    @ColumnInfo(name = "level") val level: String,
    @ColumnInfo(name = "status") var status: String,
    @ColumnInfo(name = "location") val location: String,
) : Parcelable
