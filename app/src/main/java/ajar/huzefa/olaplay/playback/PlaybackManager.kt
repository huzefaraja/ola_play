package ajar.huzefa.olaplay.playback

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.services.PlaybackService
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Actions.PLAY_SONG
import ajar.huzefa.olaplay.utility.Constants.SEEK_DURATION
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT

/**
 * This is a Singleton that manages playback.
 * It has a MediaPlayer, a PlaybackController and a MediaSession object.
 * It also has the play queue
 * These are all initialized once and then remain active for the duration of the application.
 *
 * The currently playing song is referenced using this class.
 *
 * @constructor
 * @param context the application context
 */

class PlaybackManager(val context: Context) {

    val player: MediaPlayer
        @Synchronized get() = field
    val controller: PlaybackController
        @Synchronized get() = field
    val session: MediaSessionCompat
        @Synchronized get
    var nowPlaying: Song? = null
    val queue: ArrayList<Song>
        @Synchronized get() = field

    init {
        session = MediaSessionCompat(context, Constants.Notifications.PLAYBACK_NOTIFICATION_CHANNEL)
        controller = PlaybackController(context)
        player = MediaPlayer()
        session.setCallback(controller)
        queue = ArrayList()
        session.isActive = true

    }

    companion object {
        private var instance: PlaybackManager? = null

        @Synchronized
        @JvmStatic
        fun get(context: Context): PlaybackManager {
            if (instance == null) {
                instance = PlaybackManager(context.applicationContext)
            }
            return instance!!
        }

    }

    /**
     *
     * Clears the queue and adds all the song in the given list to it
     * Updates 'nowplaying' to the current given song
     * Starts playback of the given song
     *
     * There is no check make sure the given song is in the given list
     *
     * @param list  the new queue
     * @param song  the song to play (from the list)
     *
     */
    fun setNowPlaying(list: ArrayList<Song>, song: Song) {
        queue.clear()
        for (song in list) if (!queue.contains(song)) queue.add(song)
        nowPlaying = song
        context.sendBroadcast(Intent(Constants.Broadcasts.PLAYBACK_REQUESTED))
        context.startService(Intent(context, PlaybackService::class.java).setAction(PLAY_SONG))
    }

    /**
     * This methods updates the now playing song and plays it
     */
    fun setNowPlayingSong(song: Song) {
        nowPlaying = song
        context.sendBroadcast(Intent(Constants.Broadcasts.PLAYBACK_REQUESTED))
        context.startService(Intent(context, PlaybackService::class.java).setAction(PLAY_SONG))
    }

    fun next() {
        log("next()")
        if (nowPlaying != null) {
            val song = nowPlaying
            if (queue.isNotEmpty()) {
                setNowPlayingSong(queue[(queue.indexOf(song) + 1) % queue.size])
            }
        } else {
            log("nowPlaying is null")
            if (queue.isNotEmpty()) {
                setNowPlayingSong(queue[0])
            } else {
                Toast.makeText(context, "The queue is empty!", LENGTH_SHORT).show()
            }
        }
    }

    fun previous() {
        log("previous()")
        if (nowPlaying != null) {
            val song = nowPlaying
            if (queue.isNotEmpty()) {
                var index = queue.indexOf(song) - 1
                if (index < 0) {
                    Toast.makeText(context, "At start of list!", LENGTH_SHORT).show()
                    index = 0
                }
                setNowPlayingSong(queue[index])
            }
        } else {
            log("nowPlaying is null")
            if (queue.isNotEmpty()) {
                setNowPlayingSong(queue[0])
            } else {
                Toast.makeText(context, "The queue is empty!", LENGTH_SHORT).show()
            }
        }
    }

    fun playOrPause() {
        log("playOrPause()")
        if (nowPlaying != null) {
            if (controller.isPlaying) controller.pause() else controller.start()
        } else {
            log("nowPlaying is null")
            if (queue.isNotEmpty()) {
                setNowPlayingSong(queue[0])
            } else {
                Toast.makeText(context, "The queue is empty!", LENGTH_SHORT).show()
            }
        }
    }

    fun fastForward() {
        log("previous()")
        if (nowPlaying != null) {
            controller.fastForward(SEEK_DURATION)
        } else {
            log("nowPlaying is null")
        }
    }

    fun rewind() {
        log("previous()")
        if (nowPlaying != null) {
            controller.rewind(SEEK_DURATION)
        } else {
            log("nowPlaying is null")
        }
    }

    fun playNext(song: Song) {
        log("playNext()")
        if (nowPlaying != null) {
            if (nowPlaying != song) {
                if (queue.contains(song)) queue.remove(song)
                queue.add(queue.indexOf(nowPlaying as Song) + 1, song)
            }
        } else {
            if (queue.isNotEmpty()) {
                if (queue.contains(song))
                    queue.remove(song)
            }
            queue.add(0, song)
            if (queue.size == 1)
                setNowPlayingSong(song)
        }
    }

    fun addToQueue(song: Song) {
        if (!queue.contains(song)) {
            queue.add(song)
            if (queue.size == 1)
                setNowPlayingSong(song)
        }
    }

}