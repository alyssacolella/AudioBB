package edu.temple.audiobb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "books"

/**
 * A simple [Fragment] subclass.
 * Use the [BookListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var books: ArrayList<Book>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            books = it.getParcelableArray(ARG_PARAM1) as ArrayList<Book>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_book_list, container, false)

        val recyclerView = layout.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val bookViewModel = ViewModelProvider(requireActivity())
            .get(BookViewModel::class.java)



        recyclerView.setOnClickListener {

            val itemPosition = recyclerView.getChildAdapterPosition(it)
            bookViewModel.setItem(books!![itemPosition])

        }

//        val onClickListener = View.OnClickListener {
//            val itemPosition = recyclerView.getChildAdapterPosition(it)
//            bookViewModel.setItem(books!![itemPosition])
//        }


        return layout
    }

    companion object {

        fun newInstance(param1: String, param2: String) =
            BookListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}