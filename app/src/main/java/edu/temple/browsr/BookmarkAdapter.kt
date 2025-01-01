package edu.temple.browsr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookmarkAdapter(
    private var bookmarks: List<Bookmark>,
    private val onBookmarkClick: (Bookmark) -> Unit,
    private val onBookmarkDelete: (Bookmark) -> Unit
) : RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    class BookmarkViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.bookmarkTitleTextView)
        val deleteButton: View = view.findViewById(R.id.deleteBookmarkButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bookmark, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val bookmark = bookmarks[position]
        holder.titleTextView.text = bookmark.title

        holder.itemView.setOnClickListener { onBookmarkClick(bookmark) }
        holder.deleteButton.setOnClickListener { onBookmarkDelete(bookmark) }
    }

    override fun getItemCount() = bookmarks.size

    fun updateBookmarks(newBookmarks: List<Bookmark>) {
        bookmarks = newBookmarks
        notifyDataSetChanged()
    }
}