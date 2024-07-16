package com.payment.taskmanager.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.payment.taskmanager.data.db.entity.TaskNote
import kotlinx.coroutines.flow.Flow


@Dao
abstract class TaskDao : BaseDao<TaskNote> {

    @Query("SELECT * FROM task_note")
    abstract fun all(): Flow<List<TaskNote>>

    @Query("SELECT * FROM task_note")
    abstract fun fetchUserComments(): LiveData<List<TaskNote>>

    @Query("DELETE FROM task_note")
    abstract fun clear(): Unit

    @Query("DELETE FROM task_note WHERE uniqueSlug = :uniqueSlug")
    abstract fun deleteById(uniqueSlug: Int): Int

    @Transaction
    open fun clearAndInsert(likes: List<TaskNote>) {
        clear()
        insert(likes)
    }


}