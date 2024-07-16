package com.payment.taskmanager.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.payment.taskmanager.data.db.dao.TaskDao
import kotlinx.coroutines.*
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.CoroutineContext

open class BaseViewModel : ViewModel(), CoroutineScope {

    val userDao: TaskDao by inject(TaskDao::class.java)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + Job()


    var toastMsg = MutableLiveData<String>()
    var showLoader = MutableLiveData<Boolean>()



}