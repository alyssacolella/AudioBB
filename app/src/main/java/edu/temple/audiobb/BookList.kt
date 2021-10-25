package edu.temple.audiobb

class BookList() {

    val list: MutableList<Book> = ArrayList()

    fun add(book: Book) {
        list.add(book)
    }

    fun remove(book: Book) {
        list.remove(book)
    }

    fun get(b: Int): Book {
        return list[b + 1]
    }

    fun size(): Int {
        return list.size
    }

}