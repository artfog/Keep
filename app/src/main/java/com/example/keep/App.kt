package com.example.keep

import android.app.Application
import androidx.room.Room

class App : Application() {

    val db by lazy {
        Room.databaseBuilder(this,KeepDatabase::class.java, "keep-db")
            .allowMainThreadQueries()
            .build()
    }
}