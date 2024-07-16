package com.payment.taskmanager.data.db

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Transaction
import com.payment.taskmanager.data.db.dao.TaskDao
import com.payment.taskmanager.data.db.entity.TaskNote


@Database(
    entities = [
        TaskNote::class,
    ],
    version = 1,
    exportSchema = false
)
//@TypeConverters(Converters::class)
abstract class TaskManagerDb : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    @Transaction
    suspend fun clearAll() {
        try {

            taskDao().clear()

        } catch (ex: Exception) {
            Log.e("Exception table", "$ex clear all table ex ")
        }
    }
}
