package edu.temple.audiobb

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class BookList(): Serializable {

    private val list: MutableList<Book> by lazy {
        ArrayList()
    }

    fun add(book: Book) {
        list.add(book)
    }

    fun remove(book: Book) {
        list.remove(book)
    }

    operator fun get(b: Int): Book {
        return list[b]
    }

    fun size(): Int {
        return list.size
    }

}