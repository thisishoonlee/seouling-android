package mobile.seouling.com.application.data.db

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg objects: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(objects: List<T>)

    @Update
    fun update(vararg objects: T)

    @Update
    fun update(objects: List<T>)

    @Delete
    fun delete(vararg objects: T)

    @Delete
    fun delete(objects: List<T>)
}