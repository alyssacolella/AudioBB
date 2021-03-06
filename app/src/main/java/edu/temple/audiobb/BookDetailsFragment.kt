package edu.temple.audiobb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class BookDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_details, container, false)

//        ViewModelProvider(requireActivity())
//            .get(BookViewModel:: class.java)
//            .getBook()
//            .observe(requireActivity()){
//                updateDisplay(it)
//            }

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewModelProvider(requireActivity())
            .get(BookViewModel:: class.java)
            .getBook()
            .observe(requireActivity()){
                updateDisplay(it)
            }
    }

    private fun updateDisplay(book: Book){
        view?.findViewById<TextView>(R.id.selectedTitle)?.text = book.title
        view?.findViewById<TextView>(R.id.selectedAuthor)?.text = book.author


        Log.d("SelectedTitle: ", book.title)
        Log.d("SelectedAuthor: ", book.author)

        Log.d("Title: ", view?.findViewById<TextView>(R.id.selectedTitle)?.text.toString())
        Log.d("Author: ", view?.findViewById<TextView>(R.id.selectedAuthor)?.text.toString())
    }

}
