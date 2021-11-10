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

    val volleyQueue : RequestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    var bookList = BookList()
    val newBooks = BookList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_search)

        val resultIntent = Intent().putExtra("bookList", BookList() as Serializable)
        setResult(RESULT_OK, resultIntent)

        val searchEditTextView = findViewById<TextView>(R.id.searchEditTextView)

        val dialogSearchButton = findViewById<Button>(R.id.dialogSearchButton)
        dialogSearchButton.setOnClickListener{

            fetchBooks(searchEditTextView.text.toString(), bookList)
            Log.d("Books", bookList.toString())
            Log.d("Book Item 1", bookList.get(0).toString()) //error because array is empty
            resultIntent.putExtra("bookList", bookList as Serializable)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }

    fun fetchBooks(searchText: String, books: BookList){
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


                            books.add(Book(id, title, author, imageUrl))

                            Log.d("Book just added", books.get(i).toString())
                            Log.d("Updated Book List ", bookList.toString())
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