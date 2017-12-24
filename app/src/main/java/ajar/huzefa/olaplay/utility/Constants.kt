package ajar.huzefa.olaplay.utility

import ajar.huzefa.olaplay.data.Song
import android.os.Environment
import java.io.File

/**
 * Created by huzefa on 12/17/2017.
 */
object Constants {
    object URL {
        @JvmField
        val REQUEST_SONGS = "http://starlord.hackerearth.com/studio"

        object SongParameters {
            @JvmField
            val TITLE = "song"
            @JvmField
            val URL = "url"
            @JvmField
            val ARTISTS = "artists"
            @JvmField
            val ARTWORK_URL = "cover_image"

        }
    }

    @JvmField
    val COMMA_SPACE = ", "
    @JvmField
    val AND = " & "
    @JvmField
    val SEEK_DURATION = 10000

    object Actions {
        @JvmField
        val GET_SONGS = "olaplay.request_songs"
        @JvmField
        val DOWNLOAD_SONG = "olaplay.download_song"
        @JvmField
        val PLAY_SONG = "olaplay.play_song"
        @JvmField
        val RESUME_PLAYBACK = "olaplay.resume_song"
        @JvmField
        val PAUSE_PLAYBACK = "olaplay.pause_song"
        @JvmField
        val STOP_PLAYBACK = "olaplay.stop_song"
        @JvmField
        val DOWNLOAD_PENDING = "olaplay.download_pending"
        @JvmField
        val GET_HISTORY = "olaplay.get_history"
        @JvmField
        val CLEAR_DATA = "olaplay.clear_data"
    }

    object Notifications {
        @JvmField
        val PLAYBACK_NOTIFICATION = 42
        @JvmField
        val PLAYBACK_NOTIFICATION_CHANNEL = "FORTY_TWO"
    }

    object Broadcasts {
        @JvmField
        val SONGS_LOADED_FROM_DB = "olaplay.songs_lf_db"
        @JvmField
        val LOAD_SONGS_FROM_API = "olaplay.load_songs_api"
        @JvmField
        val SONGS_REQUESTED = "olaplay.songs_requested"
        @JvmField
        val NEW_SONGS_AVAILABLE = "olaplay.nsa"
        @JvmField
        val NO_NEW_SONGS_AVAILABLE = "olaplay.nnsa"
        @JvmField
        val READY_FOR_PLAYBACK = "olaplay.ready_for_playback"
        @JvmField
        val STORAGE_PERMISSION_REQUIRED = "olaplay.spr"
        @JvmField
        val PLAYBACK_REQUESTED = "olaplay.playback_requested"
        @JvmField
        val PLAY = "olaplay.play"
        @JvmField
        val PAUSE = "olaplay.pause"
        @JvmField
        val REWIND = "olaplay.rw"
        @JvmField
        val FORWARD = "olaplay.fw"
        @JvmField
        val NEXT = "olaplay.next"
        @JvmField
        val PREVIOUS = "olaplay.prev"
        @JvmField
        val NEW_HISTORY_ITEMS_AVAILABLE = "olaplay.history_updated"
        @JvmField
        val HISTORY_RETRIEVED = "olaplay.history_retrieved"

    }

    object Tasks {
        @JvmField
        val TASK_GET_SONGS_FROM_DATABASE: Int = 4
        @JvmField
        val TASK_GET_NEW_SONGS_FROM_JSON_ARRAY: Int = 8
        @JvmField
        val TASK_GET_REDIRECTED_URLS: Int = 15
        @JvmField
        val TASK_INSERT_SONGS_INTO_DATABASE: Int = 16
        @JvmField
        val TASK_UPDATE_DOWNLOADED_SONG: Int = 23
        @JvmField
        val TASK_GET_HISTORY_FROM_DATABASE: Int = 42
        @JvmField
        val NEW_HISTORY_ITEM: Int = 108
        @JvmField
        val TASK_CLEAR_DATA_SONGS: Int = 128
        @JvmField
        val TASK_CLEAR_DATA_HISTORY: Int = 256
    }

    object Extras {
        @JvmField
        val ASYNC_TASK_ID: String = "ASYNC_TASK_ID"
        @JvmField
        val REQUEST_CODE_STORAGE_PERMISSION: Int = 42
        @JvmField
        val HISTORY_PLAY_SONG: Long = 4
        val HISTORY_LIKE_SONG: Long = 8
        val HISTORY_UNLIKE_SONG: Long = 15
        val HISTORY_DOWNLOAD_SONG: Long = 16

    }

    object Paths {
        @JvmField
        val DIR_PATH = Environment.getExternalStorageDirectory().path + File.separator + "OlaPlay"
        @JvmField
        val FILE_PATH_PREFIX = DIR_PATH + File.separator

        /**
         * Currently only for .mp3 files
         * Can be updated if required to allow any extension from the url
         * I may make the change but I'm almost out of time
         *
         */
        @JvmStatic
        fun getFilePath(song: Song): String {
            return FILE_PATH_PREFIX + "${song.title}.mp3"
        }

    }

}