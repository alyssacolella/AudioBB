package edu.temple.audiobb

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.util.Log
import java.io.BufferedInputStream
import java.io.File
import java.net.URL

class DownloadAudio(val context: Context, id: String): AsyncTask<String, String, String>() {

    var filename = id

    override fun doInBackground(vararg p0: String?): String {
        val url  = URL(p0[0])
        val connection = url.openConnection()
        connection.connect()
        val inputStream = BufferedInputStream(url.openStream())
        val filename = "${this.filename}.mp3"
        val outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE)
        val data = ByteArray(1024)
        var count = inputStream.read(data)
        var total = count
        while (count != -1) {
            outputStream.write(data, 0, count)
            count = inputStream.read(data)
            total += count
        }

        outputStream.flush()
        outputStream.close()
        inputStream.close()
        println("finished saving audio.mp3 to internal storage")
        return "Success"
    }

}