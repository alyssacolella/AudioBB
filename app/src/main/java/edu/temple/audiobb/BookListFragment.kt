package edu.temple.audiobb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


private const val ARG_PARAM1 = "books"


class BookListFragment : Fragment() {

    private lateinit var books: BookList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            books = it.getParcelable(ARG_PARAM1)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_list, container, false)

        val recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        val bookViewModel = ViewModelProvider(requireActivity())
            .get(BookViewModel::class.java)


        val onClickListener = View.OnClickListener {
            val bookPosition = recyclerView.getChildAdapterPosition(it)
            bookViewModel.setBook(books[bookPosition])
            (activity as EventInterface).selectionMade()
        }

        recyclerView.adapter = BookAdapter(books, onClickListener)

        return layout
    }

    companion object {

        fun newInstance(param1: BookList) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }

    interface EventInterface {
        fun selectionMade()
    }

}