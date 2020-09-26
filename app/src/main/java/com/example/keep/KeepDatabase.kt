package com.example.keep

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [TextItems::class])
abstract class KeepDatabase : RoomDatabase() {

    abstract fun textItemDao(): TextItemsDao

}

object Database {

    private var instance: KeepDatabase? = null

    fun getInstance(context: Context) = instance ?: Room.databaseBuilder(
        context.applicationContext, KeepDatabase::class.java, "keep-db"
    )
        .allowMainThreadQueries()
        .build()
        .also { instance = it }
}