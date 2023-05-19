package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemory

class PostViewModel(application: Application) :AndroidViewModel(application) {
    private val repository : PostRepository = PostRepositoryInMemory(application)
    private val empty = Post(
        id = 0,
        author ="",
        content ="",
        published ="",
        likedByMe = false,
        likes = 0,
        reposts = 0,
        sees = 0,
        video = ""
    )
    val edited = MutableLiveData(empty)
    val data : LiveData<List<Post>> = repository.getData()
    fun likesById(id:Long) = repository.likeById(id)
    fun repostsById(id:Long) = repository.repostById(id)
    fun seesById(id:Long) = repository.seeById(id)
    fun removeById(id:Long) = repository.removeById(id)

    fun save() {
        edited.value?.let{
            repository.save(it)
        }
        edited.value = empty
    }
    fun editById(id:Long){
        edited.value = data.value?.find {
            it.id == id
        }?.copy() ?: empty
    }

    fun changeContent(content:String){
        edited.value?.let{post->
            val text = content.trim()
            if (post.content == text)
                return
            edited.value = post.copy(content = text)
        }
    }
}