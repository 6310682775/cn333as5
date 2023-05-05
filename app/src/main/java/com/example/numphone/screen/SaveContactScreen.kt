package com.example.numphone.screen

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.mynotes.routing.MyContactsRouter
import com.example.mynotes.routing.Screen
import com.example.numphone.domain.model.ContactModel
import com.example.numphone.domain.model.NEW_CONTACT_ID
import com.example.numphone.domain.model.TagModel
import com.example.numphone.viewmodel.MainViewModel
import com.google.firebase.ktx.Firebase
//import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
//import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.launch
import java.io.File

@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalMaterialApi
@Composable
fun SaveContactScreen(viewModel: MainViewModel) {
    val contactEntry by viewModel.contactEntry.observeAsState(ContactModel())

    val tags: List<TagModel> by viewModel.tags.observeAsState(listOf())

    val bottomDrawerState = rememberBottomDrawerState(BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveContactToTrashDialogShownState = rememberSaveable { mutableStateOf(false) }

    val totalContacts  by viewModel.totalContactCount.observeAsState(Int)

    var isEditingMode by remember { mutableStateOf(false) }
    var isPhotoPicked  by remember { mutableStateOf(false) }
    var selectedImageUriSave by remember { mutableStateOf<Uri?>(null) }
    var expanded by remember { mutableStateOf(false) }

    BackHandler {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch { bottomDrawerState.close() }
        } else {
            MyContactsRouter.navigateTo(Screen.Contacts)
        }
    }

    Scaffold(
        topBar = {

            SaveNoteTopAppBar(
                onBackClick = { MyContactsRouter.navigateTo(Screen.Contacts)
                    isPhotoPicked = false},
                onSaveContactClick = { viewModel.saveContact(contactEntry)
                    isPhotoPicked = false
                    val newContact: Boolean = contactEntry.id == NEW_CONTACT_ID
                    val totalContactsAll = totalContacts as? Int ?: 0
                    fun saveImageToFirebaseStorage(imageUri: Uri,contactId : Long) {
                        viewModel.onGetTotalContactClick()
                        // Create a storage reference
                        if (newContact) {
                            val storageRef =
                                Firebase.storage.reference.child("images/${totalContactsAll + 1}.jpg")
                            val uploadTask = storageRef.putFile(imageUri)
                        } else {
                            val storageRef =
                                Firebase.storage.reference.child("images/${contactId}.jpg")
                            val uploadTask = storageRef.putFile(imageUri)
                        }
                    }
                        saveImageToFirebaseStorage( selectedImageUriSave!!,contactEntry.id)},
                onOpenTagPickerClick = {
                    coroutineScope.launch { bottomDrawerState.open() }
                },
                onDeleteContactClick = {
                    moveContactToTrashDialogShownState.value = true
                    isPhotoPicked = false                }

            )
        }
    ) {
        BottomDrawer(
            drawerState = bottomDrawerState,
            drawerContent = {
                TagPicker(
                    tags = tags,
                    onTagSelect = { tag ->
                        viewModel.onContactEntryChange(contactEntry.copy(contact_tag = tag))
                    }
                )
            }
        ) {
            SaveNoteContent(
                contact = contactEntry,
                onContactChange = { updateContactEntry ->
                    viewModel.onContactEntryChange(updateContactEntry)
                },
                isPhotoPicked = isPhotoPicked,
                onPhotoPickedClick = {
                    isPhotoPicked = true
                },
                onGetTotalContactClick = {
                    viewModel.onGetTotalContactClick()
                },
                totalContacts = totalContacts as? Int ?: 0,
                onSaveImageUriClick = {
                    uri -> selectedImageUriSave = uri
                }

            )
        }

        if (moveContactToTrashDialogShownState.value) {
            AlertDialog(
                onDismissRequest = {
                    moveContactToTrashDialogShownState.value = false
                },
                title = {
                    Text("Move note to the trash?")
                },
                text = {
                    Text(
                        "Are you sure you want to " +
                                "move this note to the trash?"
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.moveContactToTrash(contactEntry)
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        moveContactToTrashDialogShownState.value = false
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}


@Composable
fun SaveNoteTopAppBar(
    onBackClick: () -> Unit,
    onSaveContactClick: () -> Unit,
    onOpenTagPickerClick: () -> Unit,
    onDeleteContactClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Contact",
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
            IconButton(onClick = onSaveContactClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenTagPickerClick) {
                Icon(
                    painter = painterResource(id = androidx.appcompat.R.drawable.btn_radio_off_mtrl),
                    contentDescription = "Open Tag Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            IconButton(onClick = onDeleteContactClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Note Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    )
}



//@RequiresApi(Build.VERSION_CODES.Q)
@SuppressLint("SuspiciousIndentation")
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun PickPhotoFromGallery(
    contactId: Long,
    onImageChange: (String) -> Unit,
    onPhotoPickedClick: () -> Unit,
    onGetTotalContactClick: () -> Unit,
    totalContacts: Int,
    onSaveImageUriClick: (Uri) -> Unit,
                         ) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val contentResolver: ContentResolver = LocalContext.current.contentResolver
    val newContact: Boolean = contactId == NEW_CONTACT_ID
//    fun saveImageToFirebaseStorage(imageUri: Uri,contactId : Long) {
//        // Create a storage reference
//        if(newContact) {
//            val storageRef = Firebase.storage.reference.child("images/${totalContacts + 1}.jpg")
//            val uploadTask = storageRef.putFile(imageUri)
//        }else {
//            val storageRef = Firebase.storage.reference.child("images/${contactId}.jpg")
//            val uploadTask = storageRef.putFile(imageUri)
//        }
//
//    }
    // Create the activity result launcher
    val pickImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { result ->
            selectedImageUri = result
            onSaveImageUriClick(selectedImageUri!!)
            if(newContact) {
                onImageChange("${totalContacts.hashCode() + 1}.jpg")
            }else {
                onImageChange("${contactId}.jpg")
            }
        }
    )
    // Display the selected image
    selectedImageUri?.let { uri ->
        val bitmap = contentResolver.loadThumbnail(uri, Size(512, 512), null)
        if (bitmap != null) {
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
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                onPhotoPickedClick()
                // Launch the activity to pick an image from the gallery
                pickImage.launch("image/*")
            }
        ) {
            onGetTotalContactClick()
            Text(text = "Pick Image")
        }
    }
}




@RequiresApi(Build.VERSION_CODES.Q)
@Composable
private fun SaveNoteContent(
    contact: ContactModel,
    onContactChange: (ContactModel) -> Unit,
    isPhotoPicked: Boolean,
    onPhotoPickedClick: () -> Unit,
    onGetTotalContactClick: () -> Unit,
    totalContacts: Int,
    onSaveImageUriClick: (Uri) -> Unit
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
                Log.e(ContentValues.TAG, "Error downloading image: ${e.message}")
            }
        if(!isPhotoPicked) {
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
        }
        PickPhotoFromGallery(
            contactId = contact.id
        , onImageChange = { imageUri ->
                onContactChange.invoke(contact.copy(imageUri = imageUri))
            },
            onPhotoPickedClick = onPhotoPickedClick,
        onGetTotalContactClick = onGetTotalContactClick,
            totalContacts = totalContacts,
            onSaveImageUriClick = onSaveImageUriClick
        )
        ContentTextField(
            label = "name",
            text = contact.contact_name,
            onTextChange = { newName ->
                onContactChange.invoke(contact.copy(contact_name = newName))
            }
        )

        ContentTextField(
            label = "phone number",
            text = contact.phone_num,
            onTextChange = { newPhoneNum ->
                onContactChange.invoke(contact.copy(phone_num = newPhoneNum))
            }
        )

        ContentTextField(
            label = "mail",
            text = contact.mail,
            onTextChange = { newMail ->
                onContactChange.invoke(contact.copy(mail = newMail))
            }
        )

        ContentTextField(
            label = "note",
            text = contact.note,
            onTextChange = { newNote ->
                onContactChange.invoke(contact.copy(note = newNote))
            }
        )


        PickedTag(tag = contact.contact_tag)

    }
}

@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}


@Composable
private fun PickedTag(tag: TagModel) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Picked color",
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



@Composable
private fun TagPicker(
    tags: List<TagModel>,
    onTagSelect: (TagModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tag picker",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(tags.size) { itemIndex ->
                val tag = tags[itemIndex]
                TagItem(
                    tag = tag,
                    onTagSelect = onTagSelect
                )
            }
        }
    }
}

@Composable
fun TagItem(
    tag: TagModel,
    onTagSelect: (TagModel) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    onTagSelect(tag)
                }
            )
    ) {
        Text(
            text = tag.type,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

@Preview
@Composable
fun ColorItemPreview() {
    TagItem(TagModel.DEFAULT) {}
}


@Preview
@Composable
fun TagPickerPreview() {
    TagPicker(
        tags = listOf(
            TagModel.DEFAULT,
            TagModel.DEFAULT,
            TagModel.DEFAULT
        )
    ) { }
}

@Preview
@Composable
fun PickedTagPreview() {
    PickedTag(TagModel.DEFAULT)
}
