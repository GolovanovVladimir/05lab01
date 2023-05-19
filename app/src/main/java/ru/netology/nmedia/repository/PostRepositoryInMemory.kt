package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.dto.Post

class PostRepositoryInMemory(
    private  val context: Context,
) : PostRepository {

    companion object {
        private  const val FILE_NAME ="posts.json"
    }

    private val type = TypeToken.getParameterized(List::class.java, Post::class.java).type
    private val gson = Gson()
    private var posts : List<Post> = readPosts()
            set(value) {
                field = value
                sync()
            }

    private val data = MutableLiveData(posts)

    override fun getData(): LiveData<List<Post>> = data

    private  fun sync() {
        context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE).bufferedWriter().use {
            it.write(gson.toJson(posts))
        }
    }

    private fun readPosts():List<Post> {
        val file = context.filesDir.resolve(FILE_NAME)
        return if (file.exists()) {
            context.openFileInput(FILE_NAME).bufferedReader().use {
            gson.fromJson(it, type)
            }
        } else {
            emptyList()
        }
    }

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                if (post.likedByMe) {
                    post.copy(likedByMe = !post.likedByMe, likes = post.likes - 1)
                }
                else {
                    post.copy(likedByMe = !post.likedByMe, likes = post.likes + 1)
                }
            }
             else {
                post
            }
        }
        data.value = posts
    }

    override fun repostById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(reposts = post.reposts+1)
            } else {
                post
            }
        }
          data.value = posts
        }

    override fun seeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(sees = post.sees+1)
            } else {
                post
            }
        }
        data.value = posts
    }

    override fun removeById(id: Long) {
        posts = posts.filter{it.id != id}
        data.value = posts
    }

    override fun save(post: Post) {
        if(post.id == 0L) {
            posts = listOf(
                post.copy(
                    id = (posts.firstOrNull()?.id ?:0L)+1,
                    author = "Me",
                    published = "now",
                    likedByMe = false,
                )
            ) + posts
            data.value = posts
            return
        }

        posts = posts.map {
            if (post.id != it.id) it
            else it.copy(content = post.content)
            }
        data.value = posts
        }



    }