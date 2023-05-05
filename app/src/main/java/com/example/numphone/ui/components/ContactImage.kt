//package com.example.numphone.ui.components
//
//import android.content.ContentValues
//import android.graphics.Bitmap
//import android.graphics.BitmapFactory
//import android.util.Log
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material.MaterialTheme
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.SolidColor
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.semantics.Role.Companion.Image
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.Dp
//import androidx.compose.ui.unit.dp
//import com.example.numphone.database.ContactDbModel
//import com.google.firebase.storage.FirebaseStorage
//import java.io.File
//
//@Composable
//fun ContactImage(
//    contactId: Long,
//    modifier: Modifier = Modifier,
//    size: Dp,
//    border: Dp
//) {
//    val storageRef = FirebaseStorage.getInstance().reference.child("images/1.jpg")
//    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
//
//    val locale = File.createTempFile("images", "jpg")
//    storageRef.getFile(locale)
//        .addOnSuccessListener {
//            bitmap = BitmapFactory.decodeFile(locale.absolutePath)
//        }
//        .addOnFailureListener { e ->
//            Log.e(ContentValues.TAG, "Error downloading image: ${e.message}")
//        }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        bitmap?.let {
//            Image(
//                bitmap = it.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier
//                    .size(256.dp)
//                    .padding(16.dp)
//                    .clip(CircleShape)
//                    .border(1.dp, MaterialTheme.colors.primary, CircleShape),
//                contentScale = ContentScale.Crop
//            )
//        } ?: Text("Loading image...")
//    }
//}
//
//@ExperimentalMaterialApi
//@Preview
//@Composable
//private fun ContactImagePreview() {
//    ContactImage(
//        contactId = 1,
//        size = 40.dp,
//        border = 1.dp
//    )
//}