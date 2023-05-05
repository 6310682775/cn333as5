package com.example.numphone.domain.model

import androidx.room.ColumnInfo

const val NEW_CONTACT_ID = -1L

data class ContactModel(
    val id:Long = NEW_CONTACT_ID,
    val contact_name: String = "",
    val phone_num: String = "",
    val mail: String = "",
    val note: String = "",
    val isCheckedOff: Boolean? = null,
    val contact_tag: TagModel = TagModel.DEFAULT,
    val isInTrash: Boolean? =null,
    var imageUri: String? = null
    )
