package ru.netology.nmedia.repository


import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getData() : LiveData<List<Post>>
    fun likeById(id : Long)
    fun repostById(id : Long)
    fun seeById(id : Long)
    fun removeById(id:Long)
    fun save(post: Post)
}