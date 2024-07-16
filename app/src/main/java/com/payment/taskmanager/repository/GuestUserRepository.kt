package com.payment.taskmanager.repository

import androidx.lifecycle.LiveData
import com.payment.taskmanager.data.db.dao.TaskDao
import com.payment.taskmanager.data.db.entity.TaskNote

class GuestUserRepository(private val dao: TaskDao) {



    fun fetch(): LiveData<List<TaskNote>> {
        return dao.fetchUserComments()
    }

    fun updateUser(guestUser: TaskNote) {
        dao.upsert(guestUser)
    }
    fun insert(guestUser: TaskNote) {
        dao.insert(guestUser)
    }

    fun deleteUserById(userId: Int) {
        dao.deleteById(userId)
    }

    fun clearGuestUsers() {
        dao.clear()
    }
}