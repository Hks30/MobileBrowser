package edu.temple.browsr

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PageAdapter (private val pages: ArrayList<Page>, private val callback: (Int)->Unit) :  RecyclerView.Adapter<PageAdapter.PageViewHolder>(){

    inner class PageViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView) {
        init {
            textView.setOnClickListener {
                callback(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PageViewHolder(
        TextView(
            parent.context
        ).apply {
            setPadding(5,5,5,5)
            textSize = 18f
        }
    )

    override fun getItemCount() = pages.size

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.textView.text = pages[position].title
    }
}