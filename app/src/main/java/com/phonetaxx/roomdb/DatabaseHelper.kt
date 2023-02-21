package com.phonetaxx.roomdb

import android.content.Context
import androidx.room.Room

class DatabaseHelper(context: Context) {
    val appDataBase: AppDatabase


    init {
        appDataBase = Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    companion object {

        private val TAG = "DatabaseHelper"
        private val DB_NAME = "phonetaxx-db"
        private var instance: DatabaseHelper? = null

        @Synchronized
        public fun getInstance(context: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(context.applicationContext)
            }
            return instance as DatabaseHelper
        }
    }

}