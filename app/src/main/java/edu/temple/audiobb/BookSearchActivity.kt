package edu.temple.audiobb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley


class BookSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Search Activity", "launched")
        setContentView(R.layout.activity_book_search)

        findViewById<Button>(R.id.dialogSearchButton).setOnClickListener{

            val url = "https://kamorris.com/lab/cis3515/search.php?term=" +
                    findViewById<TextView>(R.id.searchEditTextView).text.toString()

            Volley.newRequestQueue(this).add(
                JsonArrayRequest(Request.Method.GET, url, null, {
                  setResult(RESULT_OK,
                      Intent().putExtra("books", BookList().apply { fillBookList(it) }))
                    Log.d("BookList from volley request", it.toString())
                    finish()
                },
                    {}))
        }
    }
}
