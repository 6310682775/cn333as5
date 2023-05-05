package com.example.numphone.database

import androidx.compose.ui.graphics.colorspace.ColorModel
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.NEW_CONTACT_ID
import com.example.numphone.domain.model.TagModel

class DbMapper {
    // Create list of NoteModels by pairing each note with a color
    fun mapContacts(
        contactDbModels: List<ContactDbModel>,
        tagDbModels: Map<Long, TagDbModel>
    ): List<ContactModel> = contactDbModels.map {
        val tagDbModel = tagDbModels[it.contact_tag]
            ?: throw RuntimeException("Color for colorId: ${it.contact_tag} was not found. Make sure that all colors are passed to this method")
        mapContact(it, tagDbModel)
    }

    // convert NoteDbModel to NoteModel
    fun mapContact(contactDbModel: ContactDbModel, tagDbModel: TagDbModel): ContactModel {
        val tag = mapTag(tagDbModel)
        val isCheckedOff = with(contactDbModel) { if (canBeCheckedOff) isCheckedOff else null }
        return with(contactDbModel) { ContactModel(id, contact_name,phone_num ,mail,note,isCheckedOff,tag ) }
    }

    // convert list of ColorDdModels to list of ColorModels
    fun mapTags(tagDbModels: List<TagDbModel>): List<TagModel> =
        tagDbModels.map { mapTag(it) }

    // convert ColorDbModel to ColorModel
    fun mapTag(tagDbModel: TagDbModel): TagModel =
        with(tagDbModel) { TagModel(id, type) }

    // convert NoteModel back to NoteDbModel
    fun mapDbContact(contact: ContactModel): ContactDbModel =
        with(contact) {
            val canBeCheckedOff = isCheckedOff != null
            val isCheckedOff = isCheckedOff ?: false
            if (id == NEW_CONTACT_ID)
                ContactDbModel(
                    contact_name = contact_name,
                    phone_num = phone_num,
                    mail = mail,
                    note = note,
                    canBeCheckedOff = canBeCheckedOff,
                    isCheckedOff = isCheckedOff,
                    contact_tag = contact_tag.id,
                    isInTrash = false,
                    imageUri = imageUri
                )
            else
                ContactDbModel(id, contact_name, phone_num, mail,note,canBeCheckedOff, isCheckedOff, contact_tag.id, false, imageUri)
        }
}