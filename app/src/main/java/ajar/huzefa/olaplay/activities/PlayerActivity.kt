package ajar.huzefa.olaplay.activities

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.fragments.SongsFragment
import ajar.huzefa.olaplay.playback.PlaybackManager
import ajar.huzefa.olaplay.playback.PlayerView
import ajar.huzefa.olaplay.receivers.ActivityReceiver
import ajar.huzefa.olaplay.utility.Constants
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_player.*

/**
 *
 * The Player Activity with controls & seekbar
 * It also has a SongsFragment for the Queue
 */
class PlayerActivity : AppCompatActivity(), ActivityReceiver.ReceiverActivity {
    override fun playbackEvent(event: String) {
        if (playerView != null) {
            playerView!!.event(event)
        }
    }


    override fun newSongsAvailable() {
        refresh()
    }

    override fun readyForPlayback() {
        playerView?.setSong(PlaybackManager.get(this).nowPlaying)
    }

    override fun playbackRequested() {
        playerView?.setSong(PlaybackManager.get(this).nowPlaying)
    }

    override fun songsLoadedFromDatabase() {
        display()
    }

    override fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), Constants.Extras.REQUEST_CODE_STORAGE_PERMISSION)
    }


    var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        initializeReceivers()
        registerReceivers()
        if (playerViewLayout != null)
            playerView = PlayerView(playerViewLayout, applicationContext)
        val playerView = playerView
        if (playerView != null) {
            if (PlaybackManager.get(this).nowPlaying != null)
                playerView.setSong(PlaybackManager.get(this).nowPlaying!!)
            else log("no now playing")
        } else log("no player view")
    }

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }


    fun display() {

    }

    fun refresh() {

    }

    var menu: Menu? = null

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.player_activity_menu, menu)
        this.menu = menu
        if (count == 0) {
        } else {

        }
        return super.onCreateOptionsMenu(menu)
    }

    var count = 0

    var fragment: Fragment? = null

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (count == 0) {
            fragment = SongsFragment.newInstance(SongsFragment.FRAGMENT_QUEUE)
            supportFragmentManager.beginTransaction().add(R.id.queueContainer, fragment).commit();
            count++
            val menu = menu
            if (menu != null && menu.size() > 0) {
                menu.getItem(0).setIcon(android.R.drawable.ic_menu_close_clear_cancel)
            }
        } else {
            supportFragmentManager.beginTransaction().remove(fragment).commit()
            count--

            val menu = menu
            if (menu != null && menu.size() > 0) {
                menu.getItem(0).setIcon(android.R.drawable.ic_menu_more)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateHistory() {
        // nothing to do here
    }

    override fun onBackPressed() {
        super.onBackPressed()
        count--
    }


    /*  Receivers  */


    private lateinit var activityReceiver: ActivityReceiver
    private val activityReceiverFilter = IntentFilter()


    private fun initializeReceivers() {
        activityReceiver = ActivityReceiver(this)
        activityReceiverFilter.addAction(Constants.Broadcasts.READY_FOR_PLAYBACK)
        activityReceiverFilter.addAction(Constants.Broadcasts.PLAYBACK_REQUESTED)
        activityReceiverFilter.addAction(Constants.Broadcasts.NEW_SONGS_AVAILABLE)
        activityReceiverFilter.addAction(Constants.Broadcasts.SONGS_LOADED_FROM_DB)
        activityReceiverFilter.addAction(Constants.Broadcasts.STORAGE_PERMISSION_REQUIRED)
        activityReceiverFilter.addAction(Constants.Broadcasts.PLAY)
        activityReceiverFilter.addAction(Constants.Broadcasts.PAUSE)
        activityReceiverFilter.addAction(Constants.Broadcasts.PREVIOUS)
        activityReceiverFilter.addAction(Constants.Broadcasts.NEXT)
        activityReceiverFilter.addAction(Constants.Broadcasts.FORWARD)
        activityReceiverFilter.addAction(Constants.Broadcasts.REWIND)
        log("activity receivers initialized")
    }

    private fun registerReceivers() {
        try {
            registerReceiver(activityReceiver, activityReceiverFilter)
            log("activity receivers registered")
        } catch (e: UninitializedPropertyAccessException) {
            // never initialized !(should never happen)
        }
    }

    private fun unregisterReceivers() {
        try {
            unregisterReceiver(activityReceiver)
            log("activity receivers unregistered")
        } catch (e: UninitializedPropertyAccessException) {
            // never initialized !(should never happen)
        }
    }

/* end receivers */
}