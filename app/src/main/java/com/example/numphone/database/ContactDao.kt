package com.example.numphone.database


import android.nfc.Tag
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

    @Query("SELECT * FROM ContactDbModel ORDER BY SUBSTR(COALESCE(contact_name, ''), 1, 1)")
    fun getAllSync(): List<ContactDbModel>

    @Query("SELECT * FROM ContactDbModel WHERE id in (:contactIds)")
    fun getContactByIdsSync(contactIds: List<Long>): List<ContactDbModel>

    @Query("SELECT * FROM ContactDbModel WHERE contact_tag Like :tag ORDER BY SUBSTR(COALESCE(contact_name, ''), 1, 1)")
    fun getContactByTagSync(tag: Long): List<ContactDbModel>

    @Query("SELECT * FROM ContactDbModel WHERE id Like :id")
    fun findByIdSync(id: Long): ContactDbModel

    @Query("SELECT * FROM ContactDbModel WHERE contact_tag Like :tag")
    fun findByTagSync(tag: Long): ContactDbModel

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    fun insert(ContactDbModel: ContactDbModel)

    @Query("SELECT COUNT(*) FROM ContactDbModel")
    fun getCountSync(): Int

    @Insert
    fun insertAll(vararg noteDbModel: ContactDbModel)

    @Query("DELETE FROM ContactDbModel WHERE id Like :id")
    fun delete(id: Long)

    @Query("DELETE FROM ContactDbModel WHERE id Like (:contactIds)")
    fun delete(contactIds: List<Long>)
}