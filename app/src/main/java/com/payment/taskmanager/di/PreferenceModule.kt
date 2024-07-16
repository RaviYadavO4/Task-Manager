package com.payment.taskmanager.di

import android.content.Context
import android.content.SharedPreferences
import com.payment.taskmanager.persistence.Prefs
import com.payment.taskmanager.persistence.Prefs.Companion.PREFS_FILE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module


val preferencesModule = module {

    single {
        providePreferences(androidContext())
    }

    single {
        Prefs(prefs = get())
    }
}

private fun providePreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)