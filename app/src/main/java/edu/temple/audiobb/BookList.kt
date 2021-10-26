package edu.temple.audiobb

import android.os.Parcel
import android.os.Parcelable

class BookList(): Parcelable {

    lateinit var list: MutableList<Book>

    constructor(parcel: Parcel) : this() {

    }

    fun add(book: Book) {
        list.add(book)
    }

    fun remove(book: Book) {
        list.remove(book)
    }

    fun get(b: Int): Book {
        return list[b]
    }

    fun size(): Int {
        return list.size
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookList> {
        override fun createFromParcel(parcel: Parcel): BookList {
            return BookList(parcel)
        }

        override fun newArray(size: Int): Array<BookList?> {
            return arrayOfNulls(size)
        }
    }


}