package com.example.shareyours.presentation.create_post_screen

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.shareyours.constants.Const
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(auth: FirebaseAuth, padding: PaddingValues, navController: NavController) {
    val context = LocalContext.current
    var selectedImage by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showClearButton by rememberSaveable { mutableStateOf(false) }
    val createPostScreenViewModel : CreatePostScreenViewModel = viewModel()
    var postText by rememberSaveable { mutableStateOf("") }
    var textSize by rememberSaveable { mutableStateOf(0) }
    var linearIndicator by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { photoUri ->
            selectedImage = photoUri
        }
    )

    Box(modifier = Modifier.padding(padding)){
        if(linearIndicator){
            LinearProgressIndicator(
                color = MaterialTheme.colorScheme.onBackground,
                trackColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.TopCenter)
                    .height(2.dp)
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 50.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Button(
                    onClick = {
                        showClearButton = false
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Fotoğraf Ekle", fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = postText.take(Const.MAX_TEXTSIZE),
                onValueChange = {
                    postText = it
                    textSize = if(postText.length <= Const.MAX_TEXTSIZE) {
                        postText.length
                    }else{
                        Const.MAX_TEXTSIZE
                    }
                },
                placeholder = { Text(text = "Düşüncelerini yaz..", color = MaterialTheme.colorScheme.onBackground) },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(width = 1.dp, color = Color.Gray),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .height(150.dp),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, keyboardType = KeyboardType.Text, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {focusManager.clearFocus()})
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd){
                Text(
                    text = "$textSize / ${Const.MAX_TEXTSIZE}",
                    color = createPostScreenViewModel.textSizeControl(textSize, MaterialTheme.colorScheme.onBackground),
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        val time = Timestamp.now()
                        if(createPostScreenViewModel.textControl(postText)){
                            createPostScreenViewModel.selectedImageControlAndOperation(
                                selectedImage = selectedImage,
                                user = auth.currentUser?.displayName ?: "",
                                text = postText,
                                time = time,
                                navController = navController
                            )
                            linearIndicator = true
                        }else {
                            Toast.makeText(context,"Gönderi Paylaşılamadı!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !linearIndicator,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                    modifier = Modifier.wrapContentSize()
                ) {
                    Text(text = "Create", fontSize = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                Box(contentAlignment = Alignment.Center){
                    AsyncImage(
                        model = selectedImage,
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.padding(8.dp)
                    )
                    LaunchedEffect(key1 = selectedImage){
                        delay(500)
                        showClearButton = true
                    }
                    if(selectedImage != null && showClearButton){
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .align(alignment = Alignment.TopEnd)
                                .background(color = MaterialTheme.colorScheme.tertiaryContainer)
                                .clickable {
                                    selectedImage = null
                                    showClearButton = false
                                }
                        ){
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier
                                    .size(16.dp)
                            )
                        }
                    }
                }
            }

        }
    }

}