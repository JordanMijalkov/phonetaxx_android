package com.phonetaxx.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phonetaxx.firebase.model.CallCategoryDbModel

@Database(entities = [CallCategoryDbModel::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getCallCategoryDao(): CallCategoryDao

}
