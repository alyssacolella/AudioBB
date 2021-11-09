package edu.temple.audiobb

import android.app.Dialog
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

class BookSearchActivity : AppCompatActivity() {

    val volleyQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    val bookListReturned = BookList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        val resultIntent = Intent().putExtra("bookList", BookList())
        setResult(RESULT_OK, resultIntent)

        val searchEditTextView = findViewById<TextView>(R.id.searchEditTextView)

        val dialogSearchButton = findViewById<Button>(R.id.dialogSearchButton)
        dialogSearchButton.setOnClickListener{

            val books = fetchBooks(searchEditTextView.text.toString())
            Log.d("Books", books.toString())
            resultIntent.putExtra("bookList", books)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

    fun fetchBooks(searchText: String): BookList {
        val url =  "https://kamorris.com/lab/cis3515/search.php?term=" + searchText

        volleyQueue.add (
            JsonArrayRequest(Request.Method.GET
                , url
                , null
                , {
                        response ->
                    try {
                        for(i in 0 until response.length()){
                            val book: JSONObject = response.getJSONObject(i)

                            val id: Int = book.getInt("id")
                            val title: String = book.getString("title")
                            val author: String = book.getString("author")
                            val imageUrl: String = book.getString("cover_url")

                            bookListReturned.add(Book(id, title, author, imageUrl))
                        }
                    } catch (e : JSONException) {
                        e.printStackTrace()
                    }
                }
                , {
                    Toast.makeText(this, "Error!", Toast.LENGTH_SHORT).show()
                })
        )
        Log.d("BookListReturned ", bookListReturned.toString())
        return bookListReturned
    }

}