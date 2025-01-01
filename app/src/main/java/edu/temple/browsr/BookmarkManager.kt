package edu.temple.browsr

import android.content.Context


class BookmarkManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("BOOKMARKS", Context.MODE_PRIVATE)

    fun saveBookmark(bookmark: Bookmark): Boolean {
        // Check if bookmark already exists
        if (prefs.contains(bookmark.url)) return false

        prefs.edit().apply {
            putString("title_${bookmark.url}", bookmark.title)
            putString("url_${bookmark.url}", bookmark.url)
            apply()
        }
        return true
    }

    fun getBookmarks(): List<Bookmark> {
        return prefs.all
            .filter { it.key.startsWith("url_") }
            .map { entry ->
                Bookmark(
                    prefs.getString("title_${entry.value}", "") ?: "",
                    entry.value.toString()
                )
            }
    }

    fun deleteBookmark(bookmark: Bookmark) {
        prefs.edit().apply {
            remove("title_${bookmark.url}")
            remove("url_${bookmark.url}")
            apply()
        }
    }
}