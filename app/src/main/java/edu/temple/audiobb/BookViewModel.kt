package edu.temple.audiobb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BookViewModel: ViewModel() {
    private val bookLiveData = MutableLiveData<Book>()

    fun getBook(): LiveData<Book> {
        return bookLiveData
    }

    fun setBook(book: Book){
        bookLiveData.value = book
    }
}