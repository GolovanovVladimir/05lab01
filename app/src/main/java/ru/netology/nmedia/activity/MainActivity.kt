package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.adaptor.PostAdaptor
import ru.netology.nmedia.adaptor.PostListener
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val prefs = getSharedPreferences("posts",Context.MODE_PRIVATE)
//        val previousResult = prefs.getString("key", null )
//        prefs.edit().apply {
//            putString("key","test")
//            apply()
//        }

        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)

//        Snackbar.make(activityMainBinding.root,previousResult.orEmpty(),Snackbar.LENGTH_LONG).show()

        val viewModel: PostViewModel by viewModels()

        val newPostContractAdd = registerForActivityResult(NewPostActivity.ContractAdd) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }
        val newPostContractEdit = registerForActivityResult(NewPostActivity.ContractEdit) { result ->
            result ?: return@registerForActivityResult
            viewModel.changeContent(result)
            viewModel.save()
        }

        val adaptor = PostAdaptor(
            object : PostListener {
                override fun onRemove(post: Post) {
                    viewModel.removeById(post.id)
                }

                override fun onEdit(post: Post) {
                 Intent().apply {
                        action = Intent.ACTION_EDIT
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    viewModel.editById(post.id)
                    newPostContractEdit.launch(post.content)
                }

                override fun onLike(post: Post) {
                    viewModel.likesById(post.id)
                }

                override fun onShare(post: Post) {
                    val intentShare = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intentShare, getString(R.string.chooser_share_post))
                    startActivity(shareIntent)
                    viewModel.repostsById(post.id)
                }

                override fun onSee(post: Post) {
                    viewModel.seesById(post.id)
                }

                override fun onViewVideo(post: Post) {
                    if (post.video.isNotBlank() && post.video.isNotEmpty() ) {
                        val intentVideo = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                        startActivity(intentVideo)
                    }
                }

            }
        )

        activityMainBinding.add.setOnClickListener{
            newPostContractAdd.launch("")
        }

        viewModel.data.observe(this)
        { posts ->
            adaptor.submitList(posts)
        }

        activityMainBinding.List.adapter = adaptor
    }
}

