package com.example.numphone.viewmodel

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.service.controls.Control
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.routing.MyContactsRouter
import com.example.mynotes.routing.Screen
import com.example.numphone.database.AppDatabase
import com.example.numphone.database.DbMapper
import com.example.numphone.database.Repository
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.TagModel
//import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : ViewModel() {

    val contactsNotInTrash: LiveData<List<ContactModel>> by lazy {
        repository.getAllContactsNotInTrash()
    }

    private var _contactEntry = MutableLiveData(ContactModel())

    val contactEntry: LiveData<ContactModel> = _contactEntry

    val tags: LiveData<List<TagModel>> by lazy {
        repository.getAllTags()
    }

    val contactsFromTagId by lazy { repository.getAllSearchFromTag() }

    val contactsInTrash by lazy { repository.getAllContactsInTrash() }

    private var _selectedContacts = MutableLiveData<List<ContactModel>>(listOf())

    val selectedContacts: LiveData<List<ContactModel>> = _selectedContacts

    val totalContactCount by lazy {repository.getTotalContactCount()}

    private val repository: Repository
    init {
        val db = AppDatabase.getInstance(application)
        repository = Repository(db.contactDao(), db.tagDao(), DbMapper())
    }

    fun onGetTotalContactClick() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.getContactCount()
        }
    }

    fun onCreateNewContactClick() {
        _contactEntry.value = ContactModel()
        MyContactsRouter.navigateTo(Screen.SaveContact)
    }
    fun onContactEditClick() {
        MyContactsRouter.navigateTo(Screen.SaveContact)
    }
    fun onContactClick(contact: ContactModel) {
        _contactEntry.value = contact
        MyContactsRouter.navigateTo(Screen.ContactInfo)
    }

    fun onContactCheckedChange(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertContact(contact)
        }
    }
    fun onSearchContactsFromTag(tagId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.searchContactsFromTag(tagId)
        }
    }


    fun onContactSelected(contact: ContactModel) {
        _selectedContacts.value = _selectedContacts.value!!.toMutableList().apply {
            if (contains(contact)) {
                remove(contact)
            } else {
                add(contact)
            }
        }
    }

    fun restoreContacts(contacts: List<ContactModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreContactsFromTrash(contacts.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContacts.value = listOf()
            }
        }
    }

    fun permanentlyDeleteContacts(contacts: List<ContactModel>) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteContacts(contacts.map { it.id })
            withContext(Dispatchers.Main) {
                _selectedContacts.value = listOf()
            }
        }
    }

    fun onContactEntryChange(contact: ContactModel) {
        _contactEntry.value = contact
    }

    fun saveContact(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertContact(contact)

            withContext(Dispatchers.Main) {
                MyContactsRouter.navigateTo(Screen.Contacts)

                _contactEntry.value = ContactModel()
            }
        }
    }

    fun moveContactToTrash(contact: ContactModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveContactToTrash(contact.id)

            withContext(Dispatchers.Main) {
                MyContactsRouter.navigateTo(Screen.Contacts)
            }
        }
    }




}