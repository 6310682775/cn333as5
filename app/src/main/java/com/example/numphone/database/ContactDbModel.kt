package com.example.numphone.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ContactDbModel(
    @PrimaryKey(autoGenerate = true) val id:Long = 0,
    @ColumnInfo(name = "contact_name") val contact_name: String,
    @ColumnInfo(name = "phone_num") val phone_num: String,
    @ColumnInfo(name = "mail") val mail: String,
    @ColumnInfo(name = "note") val note: String,
    @ColumnInfo(name = "can_be_checked_off") val canBeCheckedOff: Boolean,
    @ColumnInfo(name = "is_checked_off") val isCheckedOff: Boolean,
    @ColumnInfo(name = "contact_tag") val contact_tag: Long,
    @ColumnInfo(name = "in_trash") val isInTrash: Boolean,
    @ColumnInfo(name = "imageUri")var imageUri: String? = null
) {
    companion object{
         val DEFAULT_CONTACTS = listOf(
             ContactDbModel(1,"Chadchart ","099999999","Chadchart@fmail.com",":)",false, false, 1, false),
             ContactDbModel(2,"Prayut","099999999","Prayut@fmail.com","k",false, false, 1, false),
             ContactDbModel(3,"Pita","099999999","Pita@fmail.com","31",false, false, 6, false),         )
    }
}
