package edu.temple.audiobb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.EventInterface {

    private var twoPane = false //default to small portrait
    lateinit var bookViewModel: BookViewModel
    lateinit var bookList: BookList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //flag to determine if there are two fragment containers
        twoPane = findViewById<View>(R.id.container2) != null

        bookViewModel = ViewModelProvider(this).get(BookViewModel::class.java)

        // Pop BookDetailsFragment from stack if a book was previously selected,
        // but user has since cleared selection
        if(supportFragmentManager.findFragmentById(R.id.container1) is BookListFragment
            && bookViewModel.getBook().value == null)
                supportFragmentManager.popBackStack()

        // Remove redundant BookDetailsFragment if we're moving from single-pane mode
        // (one container) to double pane mode (two containers) and a book has been selected
        if(supportFragmentManager.findFragmentById(R.id.container1) is BookListFragment
            && twoPane)
                supportFragmentManager.popBackStack()


        //create an instance of BookList class, populate with Book objects
        bookList = generateBooks()

        // If fragment was not previously loaded (first time starting activity)
        // then add BookListFragment
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookListFragment.newInstance(bookList))
                .commit()


        // If second container is available, place an instance of BookDetailsFragment
        if(twoPane) {
            if(supportFragmentManager.findFragmentById(R.id.container2) == null)
                supportFragmentManager.beginTransaction()
                    .add(R.id.container2, BookDetailsFragment())
                    .commit()
        }
        // If moving to single-pane but a book was selected before the switch
        else if (bookViewModel.getBook().value != null){
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
        }


        supportFragmentManager.beginTransaction()
            .add(R.id.container1, BookListFragment.newInstance(bookList!!))
    }

    override fun selectionMade() {
        // only respond if there is a single container
        if (!twoPane)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .addToBackStack(null)
                .commit()
    }

    private fun generateBooks(): BookList {

        val bookList = BookList()

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

        return bookList

    }
}