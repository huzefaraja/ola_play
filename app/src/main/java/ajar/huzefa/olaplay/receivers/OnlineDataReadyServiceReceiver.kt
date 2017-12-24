package ajar.huzefa.olaplay.receivers

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.services.DataService
import ajar.huzefa.olaplay.utility.Constants
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *
 * Receiver for Data Service, to tell it to put songs in database.
 * It is not in use anymore.
 *
 */
class OnlineDataReadyServiceReceiver(var service: DataService) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        OlaPlay.log("Online Service BR")
        if (intent.action == Constants.Broadcasts.SONGS_REQUESTED) {
            service.putSongsInDB()
        }
    }
}
