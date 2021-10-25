package edu.temple.audiobb

class BookList() {

    val bookList: MutableList<Book> = ArrayList()

    fun add(book: Book) {
        bookList.add(book)
    }

    fun remove(book: Book) {
        bookList.remove(book)
    }

    fun get(b: Int): Book {
        return bookList[b + 1]
    }

    fun size(): Int {
        return bookList.size
    }
    
}