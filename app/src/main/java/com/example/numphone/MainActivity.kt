package com.example.numphone

import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mynotes.routing.MyContactsRouter
import com.example.mynotes.routing.Screen
import com.example.numphone.screen.ContactInfoScreen
import com.example.numphone.screen.ContactsScreen
import com.example.numphone.screen.SaveContactScreen
import com.example.numphone.ui.theme.MyContactsThemeSettings
import com.example.numphone.ui.theme.NumPhoneTheme
import com.example.numphone.viewmodel.MainViewModel
import com.example.numphone.viewmodel.MainViewModelFactory
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
        setContent {
            NumPhoneTheme(darkTheme = MyContactsThemeSettings.isDarkThemeEnabled) {
                val viewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(LocalContext.current.applicationContext as Application)
                )

                MainActivityScreen(viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.Q)
@ExperimentalMaterialApi
@Composable
fun MainActivityScreen(viewModel: MainViewModel) {
    Surface {
        when (MyContactsRouter.currentScreen) {
            is Screen.Contacts -> ContactsScreen(viewModel)
            is Screen.SaveContact -> SaveContactScreen(viewModel)
            is Screen.ContactInfo -> ContactInfoScreen(viewModel)
            else -> {}
        }
    }
}
