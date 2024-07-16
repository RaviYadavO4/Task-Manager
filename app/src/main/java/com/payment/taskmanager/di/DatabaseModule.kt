package com.payment.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.payment.taskmanager.data.db.TaskManagerDb
import com.payment.taskmanager.data.db.dao.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {

    single<TaskManagerDb> {
        TaskDb(androidContext = androidContext())
    }

    single { taskDao(get()) }

}


private fun taskDao(db: TaskManagerDb): TaskDao = db.taskDao()




private fun TaskDb(androidContext: Context): TaskManagerDb {
    return Room
        .databaseBuilder(androidContext, TaskManagerDb::class.java, "task.db")
        .fallbackToDestructiveMigration()
        .fallbackToDestructiveMigrationOnDowngrade()
        .fallbackToDestructiveMigrationFrom()
        .build()
}