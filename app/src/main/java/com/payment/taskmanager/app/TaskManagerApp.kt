package com.payment.taskmanager.app

import android.app.Application
import com.payment.taskmanager.di.databaseModule
import com.payment.taskmanager.di.preferencesModule
import com.payment.taskmanager.di.repoModule
import com.payment.taskmanager.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TaskManagerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        setupDI()
    }

    private fun setupDI() {

        startKoin {



            androidContext(this@TaskManagerApp)

            modules(
                listOf(
                    databaseModule,
                    preferencesModule,
                    repoModule,
                    viewModelModule
                )
            )
        }
    }

}