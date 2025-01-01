package edu.temple.browsr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class BookmarkActivity : AppCompatActivity() {
    private lateinit var bookmarkManager: BookmarkManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var bookmarkAdapter: BookmarkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        bookmarkManager = BookmarkManager(this)
        recyclerView = findViewById(R.id.bookmarkRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        bookmarkAdapter = BookmarkAdapter(
            bookmarkManager.getBookmarks(),
            onBookmarkClick = { bookmark ->
                val resultIntent = Intent().apply {
                    putExtra("bookmark_url", bookmark.url)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            },
            onBookmarkDelete = { bookmark ->
                deleteMessage(bookmark)
            }
        )
        recyclerView.adapter = bookmarkAdapter
    }

    private fun deleteMessage(bookmark: Bookmark) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Bookmark")
            .setMessage("Want to delete this bookmark?")
            .setPositiveButton("Delete") { _, _ ->
                bookmarkManager.deleteBookmark(bookmark)
                bookmarkAdapter.updateBookmarks(bookmarkManager.getBookmarks())
                Snackbar.make(recyclerView, "Bookmark deleted", Snackbar.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

