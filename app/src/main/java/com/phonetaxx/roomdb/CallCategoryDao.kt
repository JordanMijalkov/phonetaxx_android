package com.phonetaxx.roomdb

import androidx.room.*
import com.phonetaxx.firebase.model.CallCategoryDbModel

@Dao
interface CallCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllData(arrayList: List<CallCategoryDbModel?>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSingleData(categoryDbModel: CallCategoryDbModel)

    @Query("select callCategory from CallCategoryDbModel where phoneNumber like '%'||+:phNumber||+'%'")
    fun getCategoryData(phNumber: String): String

    @Query("select * from CallCategoryDbModel where phoneNumber like '%'||+:phNumber||+'%'")
    fun getCallCategoryData(phNumber: String): List<CallCategoryDbModel?>

    @Query("select * from CallCategoryDbModel")
    fun getAllData(): List<CallCategoryDbModel?>

    @Update
    fun updateCategory(categoryDbModel: CallCategoryDbModel): Int

    @Delete
    fun deleteCategory(categoryDbModel: CallCategoryDbModel): Int

    @Query("DELETE FROM CallCategoryDbModel")
    fun deleteAll()

}