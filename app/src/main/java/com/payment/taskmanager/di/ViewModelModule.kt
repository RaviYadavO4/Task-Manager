package com.payment.taskmanager.di


import com.payment.taskmanager.ui.add.LiveChannelViewModel
import com.payment.taskmanager.ui.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@ExperimentalCoroutinesApi
val viewModelModule = module {

    viewModel { BaseViewModel() }
    viewModel { LiveChannelViewModel(get()) }

}
