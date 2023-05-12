package ru.netology.nmedia.adaptor

import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.MainActivity
import ru.netology.nmedia.activity.editFlag
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Calculate
import ru.netology.nmedia.dto.Post

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener,
) : ViewHolder(binding.root) {
    fun bind(post: Post) {
        with(binding) {
            binding.authorTextView.text = post.author
            binding.dateTextView.text = post.published
            binding.aboutTextView.text = post.content
            binding.loveTextView.text = Calculate(post.likes)
            binding.repostTextView.text = Calculate(post.reposts)
            binding.seeTextView.text = Calculate(post.sees)
            binding.loveImageView.setImageResource(
                if (post.likedByMe) R.drawable.ic_baseline_liked
                else R.drawable.ic_baseline_like
            )
            binding.loveImageView.setOnClickListener {
                listener.onLike(post)
            }
            binding.repostImageView.setOnClickListener {
                listener.onShare(post)
            }
            binding.seeImageView.setOnClickListener {
                listener.onSee(post)
            }
            binding.menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_options)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }
                            R.id.edit -> {
                                editFlag = 1
                                listener.onEdit(post)
                                true
                            }
                            R.id.viewvideo -> {
                                listener.onViewVideo(post)
                                true
                            }
                            else -> false
                        } //when
                    } //setOnMenu
                } //PopupMenu
                    .show()
            }
        } //with
    } //bind
}// ViewHolder