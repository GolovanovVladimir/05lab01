package ru.netology.nmedia.dao

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {
    companion object {
        val DDL = """
        CREATE TABLE ${PostColumns.TABLE} (
            ${PostColumns.COLUMN_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${PostColumns.COLUMN_AUTHOR} TEXT NON NULL,
            ${PostColumns.COLUMN_CONTENT} TEXT NON NULL,
            ${PostColumns.COLUMN_PUBLISHED} TEXT NON NULL,
            ${PostColumns.COLUMN_LIKED_BY_ME} BOOLEAN NON NULL DEFAULT 0,
            ${PostColumns.COLUMN_LIKES} INTEGER NON NULL DEFAULT 0,
            ${PostColumns.COLUMN_REPOSTS} TEXT NON NULL DEFAULT 0,
            ${PostColumns.COLUMN_SEES} TEXT NON NULL DEFAULT 0,
            ${PostColumns.COLUMN_VIDEO} TEXT
        );
    """.trimIndent()
    }
    object PostColumns {
        const val TABLE = "posts"
        const val COLUMN_ID = "id"
        const val COLUMN_AUTHOR = "author"
        const val COLUMN_CONTENT = "content"
        const val COLUMN_PUBLISHED = "published"
        const val COLUMN_LIKED_BY_ME = "likedByMe"
        const val COLUMN_LIKES = "likes"
        const val COLUMN_REPOSTS = "reposts"
        const val COLUMN_SEES = "sees"
        const val COLUMN_VIDEO = "video"
        val ALL_COLUMNS = arrayOf(
            COLUMN_ID,
            COLUMN_AUTHOR<
            COLUMN_CONTENT,
            COLUMN_PUBLISHED,
            COLUMN_LIKED_BY_ME,
            COLUMN_LIKES,
            COLUMN_REPOSTS,
            COLUMN_SEES,
            COLUMN_VIDEO
        )
    }

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS as Array<out String>?,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()){
                posts.add(map(it))
            }
        }
        return posts
    }

    override fun likeById(id: Long) {
        db.execSQL(
                                """
                                        UPDATE posts SET
                                        likes = likes + CASE WHEN likedByMe THEN-1 ELSE 1 END,
                                        likedByMe = CASE WHEN likedById THEN 0 ELSE 1 END
                                        WHERE id = ?;
                                    """.trimIndent(), arrayOf()
                )
    }

    override fun repostById(id: Long) {
        db.execSQL(
            """
                                        UPDATE posts SET
                                        reposts = reposts + 1 
                                        WHERE id = ?;
                                    """.trimIndent(), arrayOf()
        )
    }

    override fun seeById(id: Long) {
        db.execSQL(
            """
                                        UPDATE posts SET
                                        sees = sees + 1 
                                        WHERE id = ?;
                                    """.trimIndent(), arrayOf()
        )

    }

    override fun removeById(id: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply{
            if (post.id != 0L){
                put(PostColumns.COLUMN_ID,post.id)
            }
            put(PostColumns.COLUMN_AUTHOR,"Me")
            put(PostColumns.COLUMN_CONTENT,post.content)
            put(PostColumns.COLUMN_PUBLISHED,"now")
        }
        val id = db.replace(PostColumns.TABLE,null,values)
        db.query(
            PostColumns.TABLE,
            arrayOf("${PostColumns.ALL_COLUMNS}"),
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null,
        ).use {
            it.moveToNext()
            return map(it)
        }
    }

    override fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id =  getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content =  getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published =  getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                likedByMe =  getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKED_BY_ME)) !=0,
                likes =  getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKES)),
                reposts = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_REPOSTS)),
                sees =  getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SEES)),
                video =  getString(getColumnIndexOrThrow(PostColumns.COLUMN_VIDEO)),
            )
        }
    }

}