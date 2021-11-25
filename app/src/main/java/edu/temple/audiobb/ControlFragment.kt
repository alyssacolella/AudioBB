package edu.temple.audiobb

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider

private const val ARG_PARAM1 = "param1"


class ControlFragment : Fragment() {

    lateinit var nowPlayingText: TextView
    lateinit var playButton: Button
    lateinit var pauseButton: Button
    lateinit var stopButton: Button
    lateinit var seekBar: SeekBar
    lateinit var progressText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout =  inflater.inflate(R.layout.fragment_control, container, false)
        nowPlayingText = layout.findViewById(R.id.nowPlayingText)
        playButton = layout.findViewById(R.id.playButton)
        pauseButton = layout.findViewById(R.id.pauseButton)
        stopButton = layout.findViewById(R.id.stopButton)
        seekBar = layout.findViewById(R.id.seekBar)
        progressText = layout.findViewById(R.id.progressText)

        return layout

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bookViewModel = ViewModelProvider(requireActivity()).get(SelectedBookViewModel::class.java)


        playButton.setOnClickListener {
            playButton.isEnabled = false
            pauseButton.isEnabled = true

//            val currentBook = bookViewModel.getSelectedBook().value
//            Log.d("Current book in control fragment play", currentBook.toString())

            (activity as ControlsClickedInterface).playClicked()


        }
        pauseButton.setOnClickListener {
            playButton.isEnabled = true
            pauseButton.isEnabled = false

            (activity as ControlsClickedInterface).pauseClicked()
        }
        stopButton.setOnClickListener { (activity as ControlsClickedInterface).stopClicked() }

//        seekBar.setOnSeekBarChangeListener(seekBar.progress){
//            (activity as ControlsClickedInterface).seekBarClicked()
//        }

    }

    interface ControlsClickedInterface {
        //fun playClicked(book: Book)
        fun playClicked()
        fun pauseClicked()
        fun stopClicked()
        fun seekBarClicked()

    }
}