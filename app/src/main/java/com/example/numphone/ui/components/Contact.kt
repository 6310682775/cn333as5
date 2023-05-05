package com.example.numphone.ui.components


import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numphone.database.ContactDbModel
import com.example.numphone.domain.model.ContactModel
import com.google.firebase.storage.FirebaseStorage
import java.io.File


@ExperimentalMaterialApi
@Composable
fun Contact(
    modifier: Modifier = Modifier,
    contact: ContactModel,
    onContactClick: (ContactModel) -> Unit = {},
    onContactCheckedChange: (ContactModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        val storageRef = FirebaseStorage.getInstance().reference.child("images/${contact.id}.jpg")
        var bitmap by remember { mutableStateOf<Bitmap?>(null) }

        val locale = File.createTempFile("images", "jpg")
        if (bitmap == null) { // add null check
            storageRef.getFile(locale)
                .addOnSuccessListener {
                    bitmap = BitmapFactory.decodeFile(locale.absolutePath)
                }
                .addOnFailureListener { e ->
                    Log.e(ContentValues.TAG, "Error downloading image: ${e.message}")
                }
        }
        ListItem(
            text = { Text(text = contact.contact_name, maxLines = 1) },
            secondaryText = {
                Text(text = contact.contact_tag.type, maxLines = 1)
            },

            icon = {
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colors.primary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: Box(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(2.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colors.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = MaterialTheme.colors.primary
                    )
                }
            },
            trailing = {
                if (contact.isCheckedOff != null) {
                    Checkbox(
                        checked = contact.isCheckedOff,
                        onCheckedChange = { isChecked ->
                            val newNote = contact.copy(isCheckedOff = isChecked)
                            onContactCheckedChange.invoke(newNote)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                onContactClick.invoke(contact)
            }
        )
    }
}



