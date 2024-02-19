package com.example.shareyours.presentation.create_post_screen

import android.net.Uri
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.shareyours.constants.Const
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class CreatePostScreenViewModel : ViewModel() {
    //hashmap state tutulur ve alınan fonksiyonda .valueleri verilir sonra da database kaydeder.
    val db = Firebase.firestore
    val storage = Firebase.storage

    private var _hashMapState = MutableStateFlow(CreatePostState())
    val hashMapState : StateFlow<CreatePostState> = _hashMapState.asStateFlow()


    fun textControl(postText : String): Boolean {
        return postText.isNotBlank()     //eger text boş değilse true döndürecek
    }

    fun selectedImageControlAndOperation(
        selectedImage: Uri?,
        user: String,
        text: String,
        time: Timestamp,
        navController: NavController
    ){
        if(selectedImage != null){
            addImageToStorage(selectedImage, user, text, time, navController)
        } else {
            addPostToDatabase(user = user, text = text, time = time, navController = navController)
        }
    }

    private fun addImageToStorage(
        selectedImage: Uri,
        user: String,
        text: String,
        time: Timestamp,
        navController: NavController
    ) {
        val storageRef = storage.reference //storage referansı olusturduk erisebilmek icin.
        val uid : UUID = UUID.randomUUID() //dosyayi benzersiz bir isimle kaydetmemiz gerektigi icin.
        val imageName = "${uid}.jpg"

        storageRef.child("images").child(imageName)
            .putFile(selectedImage).addOnSuccessListener { task ->
                storageRef.child("images").child(imageName).downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        addPostToDatabase(user, text, time, imageUrl, navController)
                    }
            }.addOnFailureListener { exception ->
                //Toast.makeText(context, exception.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun textSizeControl(textSize: Int, normalColor: Color): Color {
        return if(textSize == Const.MAX_TEXTSIZE) {
            Color.Red
        } else if(textSize >= 250){
            Color(0xFFFF5722)
        } else if(textSize >= 175) {
            Color(0xFFFF9800)
        } else if(textSize >= 100) {
            Color(0xFFFFC107)
        } else {
            normalColor
        }
    }

    private fun addPostToDatabase(user: String, text : String, time : Timestamp, imageUrl : String? = null, navController: NavController){
        _hashMapState.value  = CreatePostState(
            postHashMap = hashMapOf(
                "text" to text,
                "user" to user,
                "date" to time,
                "imageUrl" to imageUrl
            )
        )
        db.collection("posts")
            .add(_hashMapState.value.postHashMap)
            .addOnSuccessListener { documentReference ->
                Log.d("database", "Döküman ID: ${documentReference.id}")
                navController.navigate("homescreen"){
                    popUpTo("homescreen"){
                        inclusive = true
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("database", "Döküman Eklenemedi!", e)
            }
    }

}