package com.example.numphone.screen

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mynotes.routing.MyContactsRouter
import com.example.mynotes.routing.Screen
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.TagModel
import com.example.numphone.viewmodel.MainViewModel
import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun ContactInfoScreen(viewModel: MainViewModel) {
    val contactEntry by viewModel.contactEntry.observeAsState(ContactModel())

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveContactToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    var isEditingMode by remember { mutableStateOf(false) }


    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            MyContactsRouter.navigateTo(Screen.Contacts)
        }
    }

    Scaffold(
        topBar = {
            ContactInfoTopAppBar(
                onBackClick = { MyContactsRouter.navigateTo(Screen.Contacts) },
                onEditContactClick = {
                    viewModel.onContactEditClick()
                }
            )
        }
    ) {
        ContactInfoContent(
            contact = contactEntry,
        )
    }
}



@Composable
fun ContactInfoTopAppBar(
    onBackClick: () -> Unit,
    onEditContactClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onEditContactClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "EDIT MODE",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}




@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun ContactInfoContent(
    contact: ContactModel,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${contact.id}.jpg")
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        val locale = File.createTempFile("images", "jpg")
        storageRef.getFile(locale)
            .addOnSuccessListener {
                bitmap = BitmapFactory.decodeFile(locale.absolutePath)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error downloading image: ${e.message}")
            }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colors.primary, CircleShape),
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .size(200.dp)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colors.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(200.dp),
                    color = MaterialTheme.colors.primary
                )
            }
        }

        OutlinedTextField(
            value =  contact.contact_name,
            label = { Text("Name") },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            readOnly = true,
        )
        OutlinedTextField(
            value =  contact.phone_num,
            label = { Text("Phone number") },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            readOnly = true,
        )
        OutlinedTextField(
            value =  contact.mail,
            label = { Text("Mail") },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            readOnly = true,
        )
        OutlinedTextField(
            value =  contact.note,
            label = { Text("Note") },
            onValueChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.surface
            ),
            readOnly = true,
        )
        PickedTag(tag = contact.contact_tag)
    }
}




@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can note be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked Tag",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = tag.type,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}



