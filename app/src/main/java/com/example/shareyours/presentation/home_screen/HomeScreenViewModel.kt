package com.example.shareyours.presentation.home_screen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shareyours.Post
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class HomeScreenViewModel() : ViewModel() {
    val db = Firebase.firestore

    private val _postList = MutableStateFlow(ListState())
    val postList: StateFlow<ListState> = _postList.asStateFlow()

    @SuppressLint("SimpleDateFormat")
    private val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

    init {
        getPostList()
    }

    private fun getPostList() {
        viewModelScope.launch {
            val newPosts = mutableListOf<Post>()
            db.collection("posts")
                .orderBy("date", Query.Direction.DESCENDING) //en yeni post yukarıda olacak.
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if ((value != null) && !value.isEmpty) {
                        _postList.update { it.copy(postList = mutableListOf())}
                        for (post in value.documents) {
                            val user = post.get("user") as String
                            val text = post.get("text") as String
                            val date = post.get("date") as Timestamp
                            val imageUrl = post.get("imageUrl") as String?
                            val timestampToDate = date.toDate()
                            val formattedDate = simpleDateFormat.format(timestampToDate)
                            val createdPost = Post(
                                user = user,
                                text = text,
                                date = formattedDate,
                                imageUrl = imageUrl
                            )
                            newPosts.add(createdPost)
                        }
                        _postList.update { it.copy(postList = newPosts, isListEmpty = false) }

                        Log.d("VERICEK", "Veriler cekildi listeye eklendi!")
                    } else {
                        _postList.update { it.copy(postList = mutableListOf())}
                        Log.w("BOSERROR", "Cekilecek veri yok")
                    }
                }
            if(postList.value.postList.isEmpty()){
                _postList.update { it.copy(postList = mutableListOf(),isListEmpty = true) }
            }
        }
    }

     fun refreshHomeScreen(){
         _postList.update { it.copy(isRefreshing = true)}
         getPostList()
        _postList.update { it.copy(isRefreshing = false)}
    }
}