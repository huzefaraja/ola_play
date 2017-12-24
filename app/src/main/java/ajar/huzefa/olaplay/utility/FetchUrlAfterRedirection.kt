package ajar.huzefa.olaplay.utility

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.data.Song
import android.os.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

/**
 * The API given had redirection in the URL for playback and artwork
 * This resolves the redirection and returns the actual link in the given song objects
 */

class FetchUrlAfterRedirection(val onAsyncTaskCompletionListener: OnAsyncTaskCompletionListener?, val taskId: Int) : AsyncTask<Song, Unit, Array<out Song?>>() {
    override fun doInBackground(vararg songs: Song?): Array<out Song?> {
        log("Fetching redirected URLs in background...")
        if (songs.isNotEmpty()) {
            log("Total songs to fetch for: ${songs.size}")
            for (song in songs) {
                if (song != null) {
                    var url = URL(song.url)
                    var ucon = url.openConnection() as (HttpURLConnection)
                    ucon.instanceFollowRedirects = false
                    song.redirectedUrl = URL(ucon.getHeaderField("Location")).toString()
                    url = URL(song.coverUrl)
                    ucon = url.openConnection() as (HttpURLConnection)
                    ucon.instanceFollowRedirects = false
                    song.redirectedCoverUrl = URL(ucon.getHeaderField("Location")).toString()
                    log("For: ${song}")
                    log("Data URL: ${song.url} redirects to ${song.redirectedUrl}")
                    log("Cover URL: ${song.coverUrl} redirects to ${song.redirectedCoverUrl}")
                }
            }
        }
        return songs
    }

    override fun onPostExecute(result: Array<out Song?>?) {
        super.onPostExecute(result)
        if (onAsyncTaskCompletionListener != null) {
            onAsyncTaskCompletionListener.OnAsyncTaskCompleted(taskId, result)
        }
    }

}