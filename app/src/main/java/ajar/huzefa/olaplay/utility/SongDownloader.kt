package ajar.huzefa.olaplay.utility

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.services.DownloadService
import ajar.huzefa.olaplay.utility.Constants.Paths.DIR_PATH
import ajar.huzefa.olaplay.utility.Constants.Paths.getFilePath
import android.os.AsyncTask
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

/**
 *
 * Downloads a song and saves it to the default DIR PATH
 * with title from the getFilePath method from Constants
 *
 */

class SongDownloader(val downloadService: DownloadService, val song: Song, val downloadId: Int) : AsyncTask<Song, String, Unit>() {

    var downloaded = false

    var length = 0

    override fun doInBackground(vararg params: Song?) {
        try {
            val url = URL(song.redirectedUrl)
            val dir = File(DIR_PATH)
            if (!dir.exists()) dir.mkdir()
            val file = File(getFilePath(song))
            if (!file.exists()) file.createNewFile()
            val connection = url.openConnection()
            length = connection.contentLength
            lengthSizeString = generateSizeString(length)
            val outputStream = FileOutputStream(file)
            copyWithProgress(connection.getInputStream(), outputStream, ByteArray(DEFAULT_BUFFER_SIZE))
            downloaded = true

        } catch (e: Exception) {
            log("Error in Song Downloader: $e")
            e.printStackTrace()
        }
    }

    override fun onProgressUpdate(vararg values: String?) {
        super.onProgressUpdate(*values)
        if (values.isNotEmpty()) {
            val progressString = values[0]
            var contentText = values[1]
            if (progressString != null) {
                this.progress = progressString.toInt()
                if (contentText == null) contentText = ""
                downloadService.progressUpdate(song, this.length, this.progress, downloadId, contentText)
            }
        }
    }

    private var progress: Int = 0

    override fun onPostExecute(result: Unit?) {
        super.onPostExecute(result)
        if (downloaded) {
            downloadService.onSongDownloaded(song, lengthSizeString, downloadId)
        }
    }


    fun copyWithProgress(`in`: InputStream, out: OutputStream, buffer: ByteArray): Long {
        var read = 0L
        var n: Int
        n = `in`.read(buffer)
        while (n > 0) {
            out.write(buffer, 0, n)
            read += n.toLong()
            publishProgress(read.toString(), generateContentText())
            n = `in`.read(buffer)
        }
        out.flush()
        `in`.close()
        out.close()

        return read
    }

    lateinit var lengthSizeString: String

    private fun generateContentText(): String? {
        return "Completed %s of %s".format(generateSizeString(progress), lengthSizeString)
    }

    private fun generateSizeString(size: Int): String {
        if (size < 1024) {
            return "$size Bytes"
        } else if (size < 1048576) {
            return "%.2f KB".format(size.toDouble() / 1024)
        } else if (size < 1073741824) {
            return "%.2f MB".format(size.toDouble() / 1048576)
        } else {
            return "%.2f GB".format(size.toDouble() / 1073741824)
        }
    }
}