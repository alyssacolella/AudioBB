package edu.temple.audiobb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class BookSearchActivity : AppCompatActivity() {

    private val volleyQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    private var bookList = BookList()
    //private var bookList: BookList? = null

    //val resultIntent = Intent().putExtra("books", BookList() as Serializable)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        val resultIntent = Intent().putExtra("books", BookList() as Serializable)
        setResult(RESULT_OK, resultIntent)

        val searchEditTextView = findViewById<TextView>(R.id.searchEditTextView)

        val dialogSearchButton = findViewById<Button>(R.id.dialogSearchButton)
        dialogSearchButton.setOnClickListener{

            fetchBooks(searchEditTextView.text.toString(), bookList)
            resultIntent.putExtra("books", bookList as Serializable)

            Log.d("Books", bookList.toString())


            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    fun fetchBooks(searchText: String, books: BookList){
        val url =  "https://kamorris.com/lab/cis3515/search.php?term=" + searchText

        books.copyBooks(books)
        volleyQueue.add (
            JsonArrayRequest(Request.Method.GET
                , url
                , null
                , {
                    Log.d("Response", it.toString())
                    try {
                        for(i in 0 until it.length()){
                            val book: JSONObject = it.getJSONObject(i)

                            books.add(Book(book.getInt("id"),
                                book.getString("title"),
                                book.getString("author"),
                                book.getString("cover_url")))

                            Log.d("Book added", books.get(i).toString())
                        }

                    } catch (e : JSONException) {
                        e.printStackTrace()
                    }
                }
                , {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                })
        )
    }

}