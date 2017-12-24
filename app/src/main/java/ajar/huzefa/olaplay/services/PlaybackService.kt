package ajar.huzefa.olaplay.services

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.activities.MainActivity
import ajar.huzefa.olaplay.data.Data
import ajar.huzefa.olaplay.data.History
import ajar.huzefa.olaplay.data.InsertAsync
import ajar.huzefa.olaplay.data.UpdateAsync
import ajar.huzefa.olaplay.playback.PlaybackManager
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Notifications.PLAYBACK_NOTIFICATION
import ajar.huzefa.olaplay.utility.Constants.Notifications.PLAYBACK_NOTIFICATION_CHANNEL
import ajar.huzefa.olaplay.utility.Constants.Paths.getFilePath
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.media.session.MediaButtonReceiver
import java.io.File

/**
 *
 * This service implements the MediaPlayer listeners.
 * It also updates play count and history on playback of songs (when mediaplayer is prepared)
 *
 */

class PlaybackService : Service(), MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener {

    override fun onCreate() {
        super.onCreate()
        val player = PlaybackManager.get(this).player
        player.setOnPreparedListener(this)
        player.setOnCompletionListener(this)
        player.setOnErrorListener(this)
        player.setOnSeekCompleteListener(this)
    }

    var notification: NotificationCompat.Builder? = null

    override fun onPrepared(mediaPlayer: MediaPlayer?) {
        sendBroadcast(Intent(Constants.Broadcasts.READY_FOR_PLAYBACK))
        val song = PlaybackManager.get(this).nowPlaying
        if (song != null) {
            getNotificationBuilder()
                    .setContentTitle("${song.title} by ${song.artist}")
                    .setContentIntent(PendingIntent.getActivity(this, PLAYBACK_NOTIFICATION,
                            Intent(this, MainActivity::class.java), FLAG_UPDATE_CURRENT))

            startForeground(Constants.Notifications.PLAYBACK_NOTIFICATION, notification?.build())
            song.playCount++
            UpdateAsync(this, Constants.Broadcasts.NEW_SONGS_AVAILABLE, -1).execute(song)
            val history = History("Played ${song.title} by ${song.artist}", Constants.Extras.HISTORY_PLAY_SONG)
            val insertHistory = InsertAsync(this, Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE, Data.get(this).dataListener, Constants.Tasks.NEW_HISTORY_ITEM);
            insertHistory.execute(history)

        }
        PlaybackManager.get(this).controller.start()
    }

    fun getNotificationBuilder(): NotificationCompat.Builder {
        if (notification == null)
            notification = NotificationCompat.Builder(this, PLAYBACK_NOTIFICATION_CHANNEL)
        notification?.setSmallIcon(R.drawable.icon_ola_white)
        notification?.color = resources.getColor(R.color.colorPrimaryDark)
        return notification as NotificationCompat.Builder
    }


    override fun onSeekComplete(mp: MediaPlayer?) {
        log("MediaPlayer onSeekComplete()")
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        log("MediaPlayer onError()")
        log("$mp $what $extra")
        return true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        log("MediaPlayer onCompletion()")
        stopForeground(true)
        PlaybackManager.get(this).next()
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("PlaybackService onStartCommand()")
        if (intent != null) {
            log("onStartCommand() intent $intent")
            when (intent.action) {
                Constants.Actions.PLAY_SONG -> {
                    playSong()
                }
                Intent.ACTION_MEDIA_BUTTON -> {
                    MediaButtonReceiver.handleIntent(PlaybackManager.get(this).session, intent)
                }
            }
        } else {
            log("onStartCommand() empty intent ")
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun playSong() {
        try {
            log("playSong PlaybackService()")
            val song = PlaybackManager.get(this).nowPlaying
            val player = PlaybackManager.get(this).player
            player.reset()
            if (song != null) {
                log("playSong PlaybackService()")
                if (song.isDownloaded()) {
                    log("song is downloaded!")
                    val file = File(getFilePath(song))
                    log("${Uri.fromFile(file)}")
                    if (file.exists()) {
                        log("song file found!")
                        try {
                            player.setDataSource(applicationContext, Uri.fromFile(file))
                        } catch (e: Exception) {
                            player.setDataSource(applicationContext, Uri.parse(song.redirectedUrl))
                            log("onStartCommand() playSong() $e")
                            e.printStackTrace()
                        }
                    } else {
                        log("song file could not be found!")
                        player.setDataSource(applicationContext, Uri.parse(song.redirectedUrl))
                    }
                } else {
                    log("song is not downloaded")
                    player.setDataSource(applicationContext, Uri.parse(song.redirectedUrl))
                }
                player.prepareAsync()
            }
        } catch (e: Exception) {
            log(e)
            e.printStackTrace()
        }
    }
}
