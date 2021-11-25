package edu.temple.audiobb

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import edu.temple.audlibplayer.PlayerService

class MainActivity : AppCompatActivity(), BookListFragment.BookSelectedInterface {

    private lateinit var bookListFragment : BookListFragment

    private val searchRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        supportFragmentManager.popBackStack()
        it.data?.run {
            bookListViewModel.copyBooks(getSerializableExtra(BookList.BOOKLIST_KEY) as BookList)
            bookListFragment.bookListUpdated()
        }
    }

    var isConnected = false
    lateinit var controlsBinder: edu.temple.audlibplayer.PlayerService.MediaControlBinder

    lateinit var nowPlayingText: TextView
    lateinit var playButton: Button
    lateinit var pauseButton: Button
    lateinit var stopButton: Button
    lateinit var seekBar: SeekBar
    lateinit var progressText: TextView

    var playFromTime: Int = 0
    var endTime: Int = 0


    val progressHandler = Handler(Looper.getMainLooper()){
        progressText.text = it.arg2.toString()
        true
    }

    val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            isConnected = true
            controlsBinder = service as PlayerService.MediaControlBinder
            controlsBinder.setProgressHandler(progressHandler)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }

    private val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    private val selectedBookViewModel : SelectedBookViewModel by lazy {
        ViewModelProvider(this).get(SelectedBookViewModel::class.java)
    }

    private val bookListViewModel : BookList by lazy {
        ViewModelProvider(this).get(BookList::class.java)
    }

    companion object {
        const val BOOKLISTFRAGMENT_KEY = "BookListFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("Gets here", "")

        nowPlayingText = findViewById(R.id.nowPlayingText)!!
        playButton = findViewById(R.id.playButton)!!
        pauseButton = findViewById(R.id.pauseButton)!!
        stopButton = findViewById(R.id.stopButton)!!
        seekBar = findViewById(R.id.seekBar)!!
        progressText = findViewById(R.id.progressText)!!

        playButton.setOnClickListener{
            if(isConnected){

                //get currently selected book
                val currentBook = selectedBookViewModel.getSelectedBook().value!!

                //get the full time of the book and set the seek bar max to the end time
                endTime = currentBook.duration
                seekBar.max = endTime

                playFromTime = seekBar.progress * endTime

                //jump to correct progress, play selected book
                controlsBinder.seekTo(playFromTime)
                controlsBinder.play(currentBook.id)

                //set the now playing text
                nowPlayingText.text = "Now Playing:" + currentBook.title

                //enable pause button, disable play button
                pauseButton.isEnabled = true
                playButton.isEnabled = false
            }
        }

        pauseButton.setOnClickListener{

            //call on audlib pause function
            controlsBinder.pause()

            //set the current
            playFromTime = seekBar.progress
            pauseButton.isEnabled = false
            playButton.isEnabled = true
        }

        stopButton.setOnClickListener {
            controlsBinder.stop()
            playFromTime = 0
        }

        bindService(Intent(this, PlayerService:: class.java)
            , serviceConnection
            , BIND_AUTO_CREATE)

        // If we're switching from one container to two containers
        // clear BookDetailsFragment from container1
        if (supportFragmentManager.findFragmentById(R.id.container1) is BookDetailsFragment
            && selectedBookViewModel.getSelectedBook().value != null) {
            supportFragmentManager.popBackStack()
        }

        // If this is the first time the activity is loading, go ahead and add a BookListFragment
        if (savedInstanceState == null) {
            bookListFragment = BookListFragment()
            supportFragmentManager.beginTransaction()
                .add(R.id.container1, bookListFragment, BOOKLISTFRAGMENT_KEY)
                .commit()
        } else {
            bookListFragment = supportFragmentManager.findFragmentByTag(BOOKLISTFRAGMENT_KEY) as BookListFragment
            // If activity loaded previously, there's already a BookListFragment
            // If we have a single container and a selected book, place it on top
            if (isSingleContainer && selectedBookViewModel.getSelectedBook().value != null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment())
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // If we have two containers but no BookDetailsFragment, add one to container2
        if (!isSingleContainer && supportFragmentManager.findFragmentById(R.id.container2) !is BookDetailsFragment)
            supportFragmentManager.beginTransaction()
                .add(R.id.container2, BookDetailsFragment())
                .commit()

        findViewById<ImageButton>(R.id.searchButton).setOnClickListener {
            searchRequest.launch(Intent(this, SearchActivity::class.java))
        }

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

        controlsBinder.play(selectedBookViewModel.getSelectedBook().value!!.id)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        unbindService(serviceConnection)
//    }
}