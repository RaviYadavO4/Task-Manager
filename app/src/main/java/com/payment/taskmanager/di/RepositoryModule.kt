package com.payment.taskmanager.di


import com.payment.taskmanager.repository.GuestUserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.dsl.module


@ExperimentalCoroutinesApi
val repoModule = module {
    single { GuestUserRepository(dao = get()) }


}