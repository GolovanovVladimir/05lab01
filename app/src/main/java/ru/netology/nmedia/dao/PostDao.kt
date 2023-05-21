package ru.netology.nmedia.dao

import android.database.Cursor
import ru.netology.nmedia.dto.Post

interface PostDao {
    fun getAll() : List<Post>
    fun likeById(id : Long)
    fun repostById(id : Long)
    fun seeById(id : Long)
    fun removeById(id:Long)
    fun save(post: Post): Post
    fun map(cursor: Cursor): Post
}