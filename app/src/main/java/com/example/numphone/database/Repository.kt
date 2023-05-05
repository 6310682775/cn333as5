package com.example.numphone.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.TagModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch



class Repository(
    private val contactDao: ContactDao,
    private val tagDao: TagDao,
    private val dbMapper: DbMapper
) {

    // Working Notes
    private val contactsNotInTrashLiveData: MutableLiveData<List<ContactModel>> by lazy {
        MutableLiveData<List<ContactModel>>()
    }

    fun getAllContactsNotInTrash(): LiveData<List<ContactModel>> = contactsNotInTrashLiveData

    // Deleted Notes
    private val contactsInTrashLiveData: MutableLiveData<List<ContactModel>> by lazy {
        MutableLiveData<List<ContactModel>>()
    }

    fun getAllContactsInTrash(): LiveData<List<ContactModel>> = contactsInTrashLiveData

    private val searchFromTag: MutableLiveData<List<ContactModel>> by lazy {
        MutableLiveData<List<ContactModel>>()
    }

    fun getAllSearchFromTag(): LiveData<List<ContactModel>> = searchFromTag

    private val _totalContactCount = MutableLiveData<Int>()
    fun getTotalContactCount(): LiveData<Int> = _totalContactCount


    init {
        initDatabase(this::updateContactsLiveData)
    }

    /**
     * Populates database with colors if it is empty.
     */
    private fun initDatabase(postInitAction: () -> Unit) {
        GlobalScope.launch {
            // Prepopulate colors
            val tags = TagDbModel.DEFAULT_TAGS.toTypedArray()
            val dbTags = tagDao.getAllSync()
            if (dbTags.isNullOrEmpty()) {
                tagDao.insertAll(*tags)
            }

            // Prepopulate notes
            val contacts = ContactDbModel.DEFAULT_CONTACTS.toTypedArray()
            val dbContacts = contactDao.getAllSync()
            if (dbContacts.isNullOrEmpty()) {
                contactDao.insertAll(*contacts)
            }

            postInitAction.invoke()
        }
    }
//
    fun getContactCount(){
        val count = contactDao.getCountSync()
        _totalContactCount.postValue(count)
    }
    // get list of working notes or deleted notes
    private fun getAllContactsDependingOnTrashStateSync(inTrash: Boolean): List<ContactModel>? {
        val tagDbModels: Map<Long, TagDbModel> = tagDao.getAllSync().map { it.id to it }.toMap()
        val dbContacts: List<ContactDbModel> =
            contactDao.getAllSync().filter { it.isInTrash == inTrash }
        return dbMapper.mapContacts(dbContacts, tagDbModels)
    }

    fun insertContact(contact: ContactModel) {
        contactDao.insert(dbMapper.mapDbContact(contact))
        updateContactsLiveData()
    }

    fun deleteContacts(contactIds: List<Long>) {
        contactDao.delete(contactIds)
        updateContactsLiveData()
    }

    fun moveContactToTrash(contactId: Long) {
        val dbContact = contactDao.findByIdSync(contactId)
        val newDbContact = dbContact.copy(isInTrash = true)
        contactDao.insert(newDbContact)
        updateContactsLiveData()
    }

    fun searchContactsFromTag(tagId: Long) {
        val tag = tagDao.findByIdSync(tagId)
        val dbContacts = contactDao.getContactByTagSync(tagId)
        val contacts = dbMapper.mapContacts(dbContacts, mapOf(tag.id to tag))
        searchFromTag.postValue(contacts)
    }

    fun restoreContactsFromTrash(contactIds: List<Long>) {
        val dbContactsInTrash = contactDao.getContactByIdsSync(contactIds)
        dbContactsInTrash.forEach {
            val newDbNote = it.copy(isInTrash = false)
            contactDao.insert(newDbNote)
        }
        updateContactsLiveData()
    }

    fun getAllTags(): LiveData<List<TagModel>> =
        Transformations.map(tagDao.getAll()) { dbMapper.mapTags(it) }

    private fun updateContactsLiveData() {
        contactsNotInTrashLiveData.postValue(getAllContactsDependingOnTrashStateSync(false))
        contactsInTrashLiveData.postValue(getAllContactsDependingOnTrashStateSync(true))
    }
}