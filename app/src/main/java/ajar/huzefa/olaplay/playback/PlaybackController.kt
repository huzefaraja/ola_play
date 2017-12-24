package ajar.huzefa.olaplay.playback

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.utility.Constants
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.widget.MediaController

/**
 * Controls / listens for the MediaPlayer's events.
 * It is initialized from the PlaybackManager
 */
class PlaybackController(val context: Context) : MediaSessionCompat.Callback(), MediaController.MediaPlayerControl {

    @Synchronized
    private fun getMediaPlayer() = PlaybackManager.get(context).player

    override fun onRewind() {
        super.onRewind()
    }

    override fun onSeekTo(pos: Long) {

    }

    override fun onPlay() {
        start()
    }

    override fun onStop() {
        pause()
        seekTo(0)
    }

    override fun onPause() {
        pause()
    }

    override fun isPlaying(): Boolean {
        try {
            return getMediaPlayer().isPlaying
        } catch (e: Exception) {
            log(e)
        }
        return false
    }


    override fun canSeekForward(): Boolean {
        try {
            return true
        } catch (e: Exception) {
            log(e)
        }
        return false
    }

    override fun getDuration(): Int {
        try {
            return getMediaPlayer().duration
        } catch (e: Exception) {
            log(e)
        }
        return 0
    }

    override fun pause() {
        try {
            getMediaPlayer().pause()
            context.sendBroadcast(Intent(Constants.Broadcasts.PAUSE))
            return
        } catch (e: Exception) {
            log(e)
        }
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun seekTo(p0: Int) {
        try {
            return getMediaPlayer().seekTo(p0)
        } catch (e: Exception) {
            log(e)
        }
    }

    override fun getCurrentPosition(): Int {
        try {
            return getMediaPlayer().currentPosition
        } catch (e: Exception) {
            log(e)
        }
        return 0
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun start() {
        try {
            getMediaPlayer().start()
            context.sendBroadcast(Intent(Constants.Broadcasts.PLAY))
            return
        } catch (e: Exception) {
            log(e)
        }
    }

    override fun getAudioSessionId(): Int {
        try {
            return getMediaPlayer().audioSessionId
        } catch (e: Exception) {
            log(e)
        }
        return 0
    }

    override fun canPause(): Boolean {
        return true

    }

    fun rewind(duration: Int) {
        val position = currentPosition - duration
        if (position > 0) seekTo(position)
    }

    fun fastForward(duration: Int) {
        val position = currentPosition + duration
        if (position < getDuration()) seekTo(position)
    }
}