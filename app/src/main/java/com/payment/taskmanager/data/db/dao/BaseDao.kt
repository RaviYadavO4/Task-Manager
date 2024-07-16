package com.payment.taskmanager.data.db.dao

import androidx.room.*

@Dao
interface BaseDao<T> {
    /**
     * Insert an object in the database.
     *
     * @param obj the object to be inserted.
     * @return The SQLite row id
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: T): Long

    /**
     * Insert an array of objects in the database.
     *
     * @param obj the objects to be inserted.
     * @return The SQLite row ids
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(obj: List<@JvmSuppressWildcards T>?): List<Long>

    /**
     * Update an object from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: T)

    /**
     * Update an array of objects from the database.
     *
     * @param obj the object to be updated
     */
    @Update
    fun update(obj: List<@JvmSuppressWildcards T>?)

    /**
     * Delete an object from the database
     *
     * @param obj the object to be deleted
     */
    @Delete
    fun delete(obj: T)

    @Transaction
    fun upsert(obj: T) {
        val id = insert(obj)
        if (id == -1L) {
            update(obj)
        }
    }

    @Transaction
    open fun upsert(objList: List<@JvmSuppressWildcards T>) {
        val insertResult = insert(objList)
        val updateList: MutableList<T> = ArrayList()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) {
                updateList.add(objList[i])
            }
        }
        if (updateList.isNotEmpty()) {
            update(updateList)
        }
    }
}

@Transaction
suspend inline fun <reified T> BaseDao<T>.insertOrUpdate(item: T) {
    if (insert(item) != -1L) return
    update(item)
}
