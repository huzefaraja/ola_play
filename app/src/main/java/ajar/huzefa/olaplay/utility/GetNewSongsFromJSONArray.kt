package ajar.huzefa.olaplay.utility

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.data.Song
import android.os.AsyncTask
import org.json.JSONArray
import org.json.JSONObject

/**
 * Parses JSON and returns a collection of songs not already in the database
 */
class GetNewSongsFromJSONArray(val oldSongs: Collection<Song>, val onAsyncTaskCompletionListener: OnAsyncTaskCompletionListener?, val taskId: Int) : AsyncTask<JSONArray, Unit, ArrayList<Song>>() {
    override fun doInBackground(vararg params: JSONArray?): ArrayList<Song>? {
        if (params.isNotEmpty()) {
            val newSongs = ArrayList<Song>()
            val jsonArray = params[0]
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val json = jsonArray.getJSONObject(i)
                    if (!(exists(json)))
                        newSongs.add(Song.fromJSON(json))
                }
                return newSongs
            } else {
                return null
            }
        } else return null
    }

    fun exists(json: JSONObject): Boolean {
        log("exists()")
        log(json)
        for (song in oldSongs) {
            log(song.hash)
            if (song.hash != null)
                if (song.hash.equals(json.toString())) return true

        }
        return false
    }

    override fun onPostExecute(result: ArrayList<Song>?) {
        super.onPostExecute(result)
        if (onAsyncTaskCompletionListener != null) {
            onAsyncTaskCompletionListener.OnAsyncTaskCompleted(taskId, result)
        }
    }
}