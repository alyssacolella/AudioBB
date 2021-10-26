package edu.temple.audiobb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookAdapter(_books: BookList, _ocl: View.OnClickListener): RecyclerView.Adapter<BookAdapter.ViewHolder>() {

    val books = _books
    val ocl = _ocl

    class ViewHolder(_view: View, ocl: View.OnClickListener) : RecyclerView.ViewHolder(_view) {
        val view = _view.apply { setOnClickListener(ocl) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bookView = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_item, parent, false)
        return ViewHolder(bookView, ocl)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.findViewById<TextView>(R.id.bookName).text = books.get(position).title
        holder.view.findViewById<TextView>(R.id.authorName).text = books.get(position).author
    }

    override fun getItemCount(): Int {
        return books.size()
    }
}