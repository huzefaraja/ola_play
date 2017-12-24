package ajar.huzefa.olaplay.services

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.data.Data
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.data.UpdateAsync
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Extras.HISTORY_DOWNLOAD_SONG
import ajar.huzefa.olaplay.utility.SongDownloader
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

/**
 *
 * This service is called when a song needs to be downloaded.
 * It runs the SongDownloader AsyncTask and shows the progress in the status bar
 * It then updates history and the song and sends a broadcast on completion of download
 */

class DownloadService : Service() {

    private lateinit var notificationManager: NotificationManager
    private var downloadId = 42
    val downloaders = ArrayList<SongDownloader>()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            if (intent.action != null) {
                if (intent.action.equals(Constants.Actions.DOWNLOAD_SONG)) {
                    try {
                        downloadNextSong()
                    } catch (e: Exception) {
                        OlaPlay.log("Error in Download Service: " + e)
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun downloadNextSong() {
        if (Data.get(this).downloads.isNotEmpty()) {
            val downloads = Data.get(this).downloads
            val song = downloads[0]
            downloads.remove(song)
            downloadSong(song)
        }
    }

    fun downloadSong(song: Song) {
        val songDownloader = SongDownloader(this, song, downloadId++)
        downloaders.add(songDownloader)
        Toast.makeText(this, "Downloading ${song.title} by ${song.artist}", LENGTH_SHORT).show()
        songDownloader.execute()
    }

    fun progressUpdate(song: Song, length: Int, progress: Int, downloadId: Int, contentText: String) {
        val fileNotification = Notification.Builder(this)
                .setContentTitle("Downloading ${song.title} by ${song.artist} ($contentText)")
                .setSmallIcon(R.drawable.icon_ola)
                .setProgress(length, progress, false)
                .build()
        notificationManager.notify(downloadId, fileNotification)
    }

    fun onSongDownloaded(song: Song, size: String, downloadId: Int) {
        song.downloaded = Song.DOWNLOADED
        val fileNotification = Notification.Builder(this)
                .setContentTitle("Downloaded ${song.title} by ${song.artist} ($size)")
                .setSmallIcon(R.drawable.icon_ola)
                .build()
        notificationManager.notify(downloadId, fileNotification)

        val updateAsync = UpdateAsync(this, Constants.Broadcasts.NEW_SONGS_AVAILABLE, Constants.Tasks.TASK_UPDATE_DOWNLOADED_SONG);
        updateAsync.execute(song)
        Data.get(this).addToHistory(song, HISTORY_DOWNLOAD_SONG)

    }
}
