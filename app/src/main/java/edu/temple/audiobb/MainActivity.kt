package edu.temple.audiobb

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), BookListFragment.BookSelectedInterface {

    val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    val selectedBookViewModel : SelectedBookViewModel by lazy {
        ViewModelProvider(this).get(SelectedBookViewModel::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Grab test data
        val bookList = getBookList()

        val searchBoxButton: Button = findViewById(R.id.searchBoxButton)

        searchBoxButton.setOnClickListener(){
            val builder = AlertDialog.Builder(this)
            val inflater = this.layoutInflater
            builder.setView(inflater.inflate(R.layout.activity_book_search, null))
            builder.create()
            builder.show()
        }

        // If we're switching from one container to two containers
        // clear BookDetailsFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookDetailsFragment) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, BookListFragment.newInstance(bookList))
                .commit()
        } else
            // If activity loaded previously, there's already a BookListFragment
            // If we have a single container and a selected book, place it on top
            if (isSingleContainer && selectedBookViewModel.getSelectedBook().value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
        }

        // If we have two containers but no BookDetailsFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookDetailsFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookDetailsFragment())
                .commit()

    }

    private fun getBookList() : BookList {
        val bookList = BookList()
        val book1 = Book("the", "me", 1, "https://kamorris.com/lab/abp/covers/through_the_looking_glass.jpeg")
        bookList.add(book1)

        return bookList
    }

    override fun onBackPressed() {
        // Backpress clears the selected book
        selectedBookViewModel.setSelectedBook(null)
        super.onBackPressed()
    }

    override fun bookSelected() {
        // Perform a fragment replacement if we only have a single container
        // when a book is selected

        if (isSingleContainer) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container1, BookDetailsFragment())
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit()
        }
    }
}