package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.small_portrait)

        //create an instance of BookList class, populate with Book objects
        val bookList: BookList = BookList()

        val book1 = Book("Apples Never Fall", "Liane Moriarty")
        val book2 = Book("Vince Flynn: Enemy at the Gates", "Kyle Mills")
        val book3 = Book("Harlem Shuffle", "Colson Whitehead")
        val book4 = Book("Beautiful World, Where are You", "Sally Rooney")
        val book5 = Book("It Ends with Us", "Colleen Hoover")
        val book6 = Book("American Marxism", "Mark R. Levin")
        val book7 = Book("Unbound", "Tarana Burke")
        val book8 = Book("Fuzz", "Mary Roach")
        val book9 = Book("The Seven Husbands of Evelyn Hugo", "Taylor Jenkins Reid")
        val book10 = Book("Where the Crawdads Sing", "Delia Owens")

        bookList.add(book1)
        bookList.add(book2)
        bookList.add(book3)
        bookList.add(book4)
        bookList.add(book5)
        bookList.add(book6)
        bookList.add(book7)
        bookList.add(book8)
        bookList.add(book9)
        bookList.add(book10)


        supportFragmentManager.beginTransaction()
            .add(R.id.listContainer, BookListFragment.newInstance(bookList))


    }
}