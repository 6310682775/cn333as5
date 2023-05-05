package com.example.numphone.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mynotes.routing.Screen
import com.example.mynotes.ui.components.AppDrawer
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.TagModel
import com.example.numphone.ui.components.Contact
import com.example.numphone.viewmodel.MainViewModel
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun ContactsScreen(viewModel: MainViewModel) {
    val contacts by viewModel.contactsNotInTrash.observeAsState(listOf())
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val contactsByTagId by viewModel.contactsFromTagId.observeAsState(listOf())
    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())
    var allTag by remember { mutableStateOf(true) }
    var expanded by remember { mutableStateOf(false) }
    var showMenu by remember {mutableStateOf(false)}


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contacts",
                        color = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                },
                actions = {
                    Text(text = "Filter")
                    IconButton(onClick = {
                        showMenu = !showMenu
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.btn_radio_off_mtrl),
                            contentDescription = "Open Tag Picker Button",
                            tint = MaterialTheme.colors.onPrimary
                        )
                    }
                    val context = LocalContext.current
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false })
                    {
                        DropdownMenuItem(onClick = { showMenu = false
                            allTag = true}){
                            Text(text = "All")
                        }
                        tags.forEach() {tag ->
                            DropdownMenuItem(onClick = {
                                Toast.makeText(context, tag.type, Toast.LENGTH_SHORT).show()
                                showMenu = false
                                allTag = false
                                viewModel.onSearchContactsFromTag(tag.id)}) {
                                Text(text = tag.type)
                            }
                        }
                    }
                }
            )
        },
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Contacts,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewContactClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note Button"
                    )
                }
            )
        }
    ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (allTag) {
                    ContactsList(
                        contacts = contacts,
                        onContactCheckedChange = {
                            viewModel.onContactCheckedChange(it)
                        },
                        onContactClick = { viewModel.onContactClick(it) }
                    )
                }
                if (!allTag) {
                    ContactsList(
                        contacts = contactsByTagId,
                        onContactCheckedChange = {
                            viewModel.onContactCheckedChange(it)
                        },
                        onContactClick = { viewModel.onContactClick(it) }
                    )
                }
            }
        }

    }






@ExperimentalMaterialApi
@Composable
private fun ContactsList(
    contacts: List<ContactModel>,
    onContactCheckedChange: (ContactModel) -> Unit,
    onContactClick: (ContactModel) -> Unit
) {
    LazyColumn {
        items(count = contacts.size) { contactIndex ->
            val contact = contacts[contactIndex]
            Contact(
                contact = contact,
                onContactClick = onContactClick,
                onContactCheckedChange = onContactCheckedChange,
                isSelected = false

            )
        }
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun NotesListPreview() {
    ContactsList(
        contacts = listOf(
                    ContactModel(1, "Note 1", "Content 1", ),
                     ContactModel(2, "Note 2", "Content 1", ),
                ContactModel(3, "Note 3", "Content 1", ),
        ContactModel(4, "Note 4", "Content 1", )
        ),
        onContactCheckedChange = {},
        onContactClick = {}
    )
}