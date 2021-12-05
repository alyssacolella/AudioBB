package edu.temple.audiobb

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import edu.temple.audlibplayer.PlayerService
import java.io.*

private const val SAVED_PROGRESS_KEY = "saved_progress"

class MainActivity : AppCompatActivity(), BookListFragment.BookSelectedInterface , ControlFragment.MediaControlInterface{

    private lateinit var bookListFragment : BookListFragment
    private lateinit var serviceIntent : Intent
    private lateinit var mediaControlBinder : PlayerService.MediaControlBinder
    private var connected = false
    private lateinit var preferences: SharedPreferences
    //val preferences: SharedPreferences = getSharedPreferences(SAVED_PROGRESS_KEY, Context.MODE_PRIVATE)

    private lateinit var progressArray: ProgressArray

    //var savedProgressFile = File(filesDir, "SavedProgress")
    lateinit var savedProgressFile: File

    val audiobookHandler = Handler(Looper.getMainLooper()) { msg ->

        // obj (BookProgress object) may be null if playback is paused
        msg.obj?.let { msgObj ->
            val bookProgress = msgObj as PlayerService.BookProgress
            // If the service is playing a book but the activity doesn't know about it
            // (this would happen if the activity was closed and then reopened) then
            // fetch the book details so the activity can be properly updated
            if (playingBookViewModel.getPlayingBook().value == null) {
                Volley.newRequestQueue(this)
                    .add(JsonObjectRequest(Request.Method.GET, API.getBookDataUrl(bookProgress.bookId), null, { jsonObject ->
                        playingBookViewModel.setPlayingBook(Book(jsonObject))
                        // If no book is selected (if activity was closed and restarted)
                        // then use the currently playing book as the selected book.
                        // This allows the UI to display the book details
                        if (selectedBookViewModel.getSelectedBook().value == null) {
                            // set book
                            selectedBookViewModel.setSelectedBook(playingBookViewModel.getPlayingBook().value)
                            // display book - this function was previously implemented as a callback for
                            // the BookListFragment, but it turns out we can use it here - Don't Repeat Yourself
                            bookSelected()
                        }
                    }, {}))
            }

            progressArray.times.put(selectedBookViewModel.getSelectedBook().value!!.id, bookProgress.progress)
            Log.d("Time in sparse array", progressArray.times.get(selectedBookViewModel.getSelectedBook().value!!.id).toString())

            // Everything that follows is to prevent possible NullPointerExceptions that can occur
            // when the activity first loads (after config change or opening after closing)
            // since the service can (and will) send updates via the handler before the activity fully
            // loads, the currently playing book is downloaded, and all variables have been initialized
            supportFragmentManager.findFragmentById(R.id.controlFragmentContainerView)?.run{
                with (this as ControlFragment) {
                    playingBookViewModel.getPlayingBook().value?.also {

                        var progress = ((bookProgress.progress / it.duration.toFloat()) * 100).toInt()
                        setPlayProgress(progress)

                        progressArray.times.put(playingBookViewModel.getPlayingBook().value!!.id, bookProgress.progress)
                    }
                }
            }
        }

        true
    }

    private val searchRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        supportFragmentManager.popBackStack()
        it.data?.run {
            bookListViewModel.copyBooks(getSerializableExtra(BookList.BOOKLIST_KEY) as BookList)
            bookListFragment.bookListUpdated()
        }

    }

    private val serviceConnection = object: ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mediaControlBinder = service as PlayerService.MediaControlBinder
            mediaControlBinder.setProgressHandler(audiobookHandler)
            connected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            connected = false
        }

    }

    private val isSingleContainer : Boolean by lazy{
        findViewById<View>(R.id.container2) == null
    }

    private val selectedBookViewModel : SelectedBookViewModel by lazy {
        ViewModelProvider(this).get(SelectedBookViewModel::class.java)
    }

    private val playingBookViewModel : PlayingBookViewModel by lazy {
        ViewModelProvider(this).get(PlayingBookViewModel::class.java)
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

        savedProgressFile = File(filesDir, "SavedProgress")
        if(!savedProgressFile.exists()){
            savedProgressFile.createNewFile()
        }

//        var fis = FileInputStream(savedProgressFile)
//        var ois = ObjectInputStream(fis)
//        if(ois.readObject() as ProgressArray != null){
//            recordedTimes = ois.readObject() as ProgressArray
//        }
//        ois.close()
//        fis.close()

//        var t = SparseArray<Int>()
//        recordedTimes = ProgressArray(t)

        preferences = getSharedPreferences(SAVED_PROGRESS_KEY, Context.MODE_PRIVATE)

        playingBookViewModel.getPlayingBook().observe(this, {
            (supportFragmentManager.findFragmentById(R.id.controlFragmentContainerView) as ControlFragment).setNowPlaying(it.title)
        })

        // Create intent for binding and starting service
        serviceIntent = Intent(this, PlayerService::class.java)

        // bind to service
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE)

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
        // Back press clears the selected book
        saveTimesToFile()
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

    override fun play() {
        if (connected && selectedBookViewModel.getSelectedBook().value != null) {

            Log.d("Button pressed", "Play button")

            val selectedBook = selectedBookViewModel.getSelectedBook().value
            var selectedUrl = "https://kamorris.com/lab/audlib/download.php?id=" + selectedBook!!.id

            if(!::progressArray.isInitialized){
                progressArray = ProgressArray(SparseArray())
            }

            if(progressArray.times.get(selectedBook.id) == null){
                Log.d("gets", "here")
                progressArray.times.put(selectedBook.id, 0)
            }

            var fis = FileInputStream(savedProgressFile)
            if(fis.read() != -1){
                var ois = ObjectInputStream(fis)
                if(ois.readObject() as ProgressArray != null){
                    progressArray = ois.readObject() as ProgressArray
                    Log.d("Progress after reading in", progressArray.times.get(selectedBookViewModel.getSelectedBook().value!!.id).toString())
                }
                ois.close()
                fis.close()
            }

            //play from file if it exists in internal storage
            if(fileExists("${selectedBook!!.id}.mp3")){
                Log.d("Playing", "downloaded file")
                //var time = preferences.getInt("${selectedBook!!.id}_key", 0)
                Log.d("Time in array", progressArray.times.get(selectedBook.id).toString())
                mediaControlBinder.play(File(filesDir, "${selectedBook!!.id}.mp3"), progressArray.times.get(selectedBook.id))
            }
            else{ //if file doesn't exist, stream it for now and download the mp3 file
                Log.d("Book", "not downloaded yet")
                mediaControlBinder.seekTo(progressArray.times.get(selectedBook.id))
                mediaControlBinder.play(selectedBook!!.id)
                DownloadAudio(this, selectedBook!!.id.toString()).execute(selectedUrl)
            }

            playingBookViewModel.setPlayingBook(selectedBook)
            startService(serviceIntent)
        }
    }

    override fun pause() {
        if (connected) {
            mediaControlBinder.pause()
            Log.d("Time in array after pause", progressArray.times.get(selectedBookViewModel.getSelectedBook().value!!.id).toString())
        }
    }

    override fun stop() {
        if (connected) {
            //set progress back to zero
            progressArray.times.put(selectedBookViewModel.getSelectedBook().value!!.id, 0)

            mediaControlBinder.stop()
            stopService(serviceIntent)
        }
    }

    override fun seek(position: Int) {
        // Converting percentage to proper book progress
        if (connected) mediaControlBinder.seekTo((playingBookViewModel.getPlayingBook().value!!.duration * (position.toFloat() / 100)).toInt())
    }

    fun fileExists(fileName: String): Boolean {
        val path: String = this.filesDir.absolutePath.toString() + "/" + fileName
        Log.d("File path", path)

        val file = File(path)
        return file.exists()
    }

    fun saveTimesToFile(){
        var fos = openFileOutput(savedProgressFile.name, Context.MODE_PRIVATE)
        var oos = ObjectOutputStream(fos)

        oos.writeObject(progressArray)
        oos.close()
        fos.close()
    }

    override fun onDestroy() {
        saveTimesToFile()
        super.onDestroy()
        unbindService(serviceConnection)
    }
}
