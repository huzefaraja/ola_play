package ajar.huzefa.olaplay.receivers

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.utility.Constants
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *
 * A receiver for all events the two activities need to be ready for
 *
 */
class ActivityReceiver(val activity: ReceiverActivity) : BroadcastReceiver() {

    interface ReceiverActivity {
        fun newSongsAvailable()
        fun readyForPlayback()
        fun playbackRequested()
        fun songsLoadedFromDatabase()
        fun requestStoragePermission()
        fun playbackEvent(event: String)
        fun updateHistory()
    }

    override fun onReceive(context: Context, intent: Intent) {
        OlaPlay.log("NMP Activity BR")
        when (intent.action) {
            Constants.Broadcasts.PLAYBACK_REQUESTED -> activity.playbackRequested()
            Constants.Broadcasts.READY_FOR_PLAYBACK -> activity.readyForPlayback()
            Constants.Broadcasts.NEW_SONGS_AVAILABLE -> activity.newSongsAvailable()
            Constants.Broadcasts.SONGS_LOADED_FROM_DB -> activity.songsLoadedFromDatabase()
            Constants.Broadcasts.STORAGE_PERMISSION_REQUIRED -> activity.requestStoragePermission()
            Constants.Broadcasts.PLAY -> activity.playbackEvent(Constants.Broadcasts.PLAY)
            Constants.Broadcasts.PAUSE -> activity.playbackEvent(Constants.Broadcasts.PAUSE)
            Constants.Broadcasts.REWIND -> activity.playbackEvent(Constants.Broadcasts.REWIND)
            Constants.Broadcasts.FORWARD -> activity.playbackEvent(Constants.Broadcasts.FORWARD)
            Constants.Broadcasts.NEXT -> activity.playbackEvent(Constants.Broadcasts.NEXT)
            Constants.Broadcasts.PREVIOUS -> activity.playbackEvent(Constants.Broadcasts.PREVIOUS)
            Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE -> activity.updateHistory()
            Constants.Broadcasts.HISTORY_RETRIEVED -> activity.updateHistory()
        }
    }
}
