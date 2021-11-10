package edu.temple.audiobb

import android.util.Log
import androidx.lifecycle.ViewModel
import org.json.JSONArray
import java.io.Serializable

class BookList : ViewModel(), Serializable{

    private val bookList : MutableList<Book> by lazy {
        ArrayList()
    }

    fun add(book: Book) {
        bookList.add(book)
    }

    fun remove(book: Book){
        bookList.remove(book)
    }

    operator fun get(index: Int) = bookList[index]

    fun size() = bookList.size

    fun copyBooks(newBooks: BookList){
        bookList.clear()
        bookList.addAll(newBooks.bookList)
    }

    fun fillBookList(books: JSONArray){
        for(i in 0 until books.length()){
            val book = books.getJSONObject(i)
            bookList.add(Book(book.getInt("id"),
                book.getString("title"),
                book.getString("author"),
                book.getString("cover_url")))
        }
        Log.d("BookList in fillBookList()", bookList.toString())

    }
}