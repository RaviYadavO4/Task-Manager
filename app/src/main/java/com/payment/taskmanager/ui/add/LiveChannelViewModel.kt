package com.payment.taskmanager.ui.add

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.payment.taskmanager.data.db.entity.TaskNote
import com.payment.taskmanager.repository.GuestUserRepository
import com.payment.taskmanager.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LiveChannelViewModel(
    private val userCommentRepository: GuestUserRepository,
) : BaseViewModel() {

    private val dueDate = mutableSetOf<String>()


    var userComments: LiveData<List<TaskNote>>? = null

    init {
        userComments = userCommentRepository.fetch()
    }

    fun updateUser(guestUser: TaskNote) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userCommentRepository.updateUser(guestUser)
            }catch (e:Exception){
                Log.v("Exception=========","$e")
                e.printStackTrace()
            }
        }
    }
    fun insertUser(guestUser: TaskNote) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userCommentRepository.insert(guestUser)
            }catch (e:Exception){
                Log.v("Exception=========","$e")
                e.printStackTrace()
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                userCommentRepository.deleteUserById(userId)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }


    fun updateUserComments(userComment: TaskNote) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
            userCommentRepository.updateUser(userComment)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    fun deleteUserComments() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
            userCommentRepository.clearGuestUsers()
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}