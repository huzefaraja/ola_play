package ajar.huzefa.olaplay.activities

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.adapters.ViewPagerAdapter
import ajar.huzefa.olaplay.data.Data
import ajar.huzefa.olaplay.fragments.HistoryFragment
import ajar.huzefa.olaplay.fragments.HistoryFragment.Companion.FRAGMENT_HISTORY
import ajar.huzefa.olaplay.fragments.LovedChild
import ajar.huzefa.olaplay.fragments.LovingParent
import ajar.huzefa.olaplay.fragments.SongsFragment
import ajar.huzefa.olaplay.fragments.SongsFragment.Companion.FRAGMENT_ALL_SONGS
import ajar.huzefa.olaplay.fragments.SongsFragment.Companion.FRAGMENT_DOWNLOADED_SONGS
import ajar.huzefa.olaplay.fragments.SongsFragment.Companion.FRAGMENT_FILTERED_SONGS
import ajar.huzefa.olaplay.fragments.SongsFragment.Companion.FRAGMENT_LIKED_SONGS
import ajar.huzefa.olaplay.fragments.SongsFragment.Companion.FRAGMENT_MOST_PLAYED_SONGS
import ajar.huzefa.olaplay.playback.PlaybackManager
import ajar.huzefa.olaplay.playback.PlayerView
import ajar.huzefa.olaplay.receivers.ActivityReceiver
import ajar.huzefa.olaplay.services.DataService
import ajar.huzefa.olaplay.services.DownloadService
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Extras.REQUEST_CODE_STORAGE_PERMISSION
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


/**
 *
 * It has a view pager with six pages (All Songs, Downloaded, Liked, Search, Most Played,
 * and History)
 *
 * It has two menu items - search and clear data
 */

class MainActivity : AppCompatActivity(), View.OnClickListener, ActivityReceiver.ReceiverActivity, LovingParent, ViewPager.OnPageChangeListener, DialogInterface.OnClickListener {
    override fun onClick(dialog: DialogInterface?, which: Int) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            startService(Intent(this, DataService::class.java).setAction(Constants.Actions.CLEAR_DATA))
            progressBarContainer.visibility = View.VISIBLE
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position == 4) {
            updateHistory()
        }
    }

    override fun theResponsibleParent(child: LovedChild) {
        children[child.getFragmentType()] = child
    }

    val children = HashMap<Int, LovedChild?>()

    init {
        children.put(FRAGMENT_ALL_SONGS, null)
        children.put(FRAGMENT_DOWNLOADED_SONGS, null)
        children.put(FRAGMENT_LIKED_SONGS, null)
        children.put(FRAGMENT_FILTERED_SONGS, null)
        children.put(FRAGMENT_MOST_PLAYED_SONGS, null)
        children.put(FRAGMENT_HISTORY, null)

    }

    override fun playbackEvent(event: String) {
        playerView?.event(event)
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
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_STORAGE_PERMISSION)
    }


    override fun onClick(v: View?) {
        if (v == playerBottomBar) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (playerView != null) {
                    playerView?.run {

                        startActivity(Intent(applicationContext, PlayerActivity::class.java),
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this@MainActivity,
                                        android.support.v4.util.Pair(playPauseButton, playPauseButton?.transitionName),
                                        android.support.v4.util.Pair(titleTextView, titleTextView?.transitionName),
                                        android.support.v4.util.Pair(artistTextView, artistTextView?.transitionName),
                                        android.support.v4.util.Pair(artImageView, artImageView?.transitionName),
                                        android.support.v4.util.Pair(forwardButton, forwardButton?.transitionName),
                                        android.support.v4.util.Pair(nextButton, nextButton?.transitionName),
                                        android.support.v4.util.Pair(rewindButton, rewindButton?.transitionName),
                                        android.support.v4.util.Pair(previousButton, previousButton?.transitionName),
                                        android.support.v4.util.Pair(playerBottomBar, "card"),
                                        android.support.v4.util.Pair(container, "container")
                                ).toBundle())

                    }
                } else {
                    startActivity(Intent(applicationContext, PlayerActivity::class.java))
                }
            } else {
                startActivity(Intent(applicationContext, PlayerActivity::class.java))
            }
        }

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                startService(Intent(this, DownloadService::class.java).setAction(Constants.Actions.DOWNLOAD_PENDING))
            }
        }
    }

    var playerView: PlayerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        log("Content View Set")
        val supportActionBar = supportActionBar
        log(supportActionBar)
        if (supportActionBar != null) {
            supportActionBar.setHomeAsUpIndicator(R.drawable.icon_ola)
        } else {

        }

        initializeReceivers()
        registerReceivers()
        initializePlayerView()
        setViewPagerAdapter()

        // datatest()

        request()
    }


    private fun initializePlayerView() {
        playerView = PlayerView(playerBottomBar, applicationContext)
        if (PlaybackManager.get(this).nowPlaying != null) {
            playerView?.setSong(PlaybackManager.get(this).nowPlaying)
        }
        playerBottomBar.setOnClickListener(this)

    }


    private lateinit var viewPagerAdapter: ViewPagerAdapter

    fun conceive(type: Int): LovedChild {
        log("conceive($type)")
        if (type == FRAGMENT_HISTORY) return HistoryFragment.newInstance()
        else return SongsFragment.newInstance(type)
    }

    fun getTitle(key: Int): String {
        log("conceive($key)")
        return when (key) {
            FRAGMENT_ALL_SONGS -> "All Songs"
            FRAGMENT_DOWNLOADED_SONGS -> "Downloaded"
            FRAGMENT_LIKED_SONGS -> "Liked"
            FRAGMENT_FILTERED_SONGS -> "Search"
            FRAGMENT_MOST_PLAYED_SONGS -> "Most Played"
            FRAGMENT_HISTORY -> "History"
            else -> "All Songs"
        }
    }

    fun nameChild(key: Int): NamedChild {
        log("nameChild($key)")
        if (children[key] == null) children[key] = conceive(key)
        val child = children[key]
        return NamedChild(child!!.asFragment(), getTitle(key));
    }

    private fun getViewPagerAdapter(): ViewPagerAdapter {

        val namedChildren = ArrayList<NamedChild>()
        namedChildren.add(nameChild(FRAGMENT_ALL_SONGS))
        namedChildren.add(nameChild(FRAGMENT_DOWNLOADED_SONGS))
        namedChildren.add(nameChild(FRAGMENT_LIKED_SONGS))
        namedChildren.add(nameChild(FRAGMENT_FILTERED_SONGS))
        namedChildren.add(nameChild(FRAGMENT_MOST_PLAYED_SONGS))
        namedChildren.add(nameChild(FRAGMENT_HISTORY))

        log("getViewPagerAdapter()")
        try {
            return viewPagerAdapter
        } catch (e: UninitializedPropertyAccessException) {
            log("viewPagerAdapter is not ready, creating...")
            val fragments = ArrayList<Fragment>()
            val titles = ArrayList<String>()
            if (namedChildren.isNotEmpty()) {
                for (titledFragment in namedChildren) {
                    fragments.add(titledFragment.fragment)
                    titles.add(titledFragment.title)
                }

            }
            viewPagerAdapter = ViewPagerAdapter(supportFragmentManager, fragments, titles)
            return viewPagerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menuItemSearch)
            viewPager?.currentItem = 3
        else if (item?.itemId == R.id.menuItemClearData)
            AlertDialog.Builder(this).setMessage("Are you sure you want to clear app data? Any songs downloaded to the device will be preserved.")
                    .setPositiveButton("Yes", this).setNegativeButton("No", this)
                    .create().show()
        return super.onOptionsItemSelected(item)
    }

    private fun setViewPagerAdapter() {
        log("setViewPagerAdapter()")
        viewPager?.adapter = getViewPagerAdapter()
        viewPager?.addOnPageChangeListener(this)
    }

    data class NamedChild(val fragment: Fragment, val title: String)


    private fun request() {
        log("Start data service with GET_SONGS")
        progressBarContainer.visibility = View.VISIBLE
        startService(Intent(applicationContext, DataService::class.java).setAction(Constants.Actions.GET_SONGS))
    }

    fun display() {
        refresh()
        if (Data.get(this).songs.size <= 0) {
            progressBarContainer.visibility = View.VISIBLE
        }
    }

    fun refresh() {
        for (child in children)
            try {
                child.value?.refresh()
            } catch (e: UninitializedPropertyAccessException) {
                log("The fragment references have not been initialized! Can't call refresh()")
            }
        progressBarContainer.visibility = View.GONE
    }

    override fun onDestroy() {
        unregisterReceivers()
        super.onDestroy()
    }


/*  Receivers  */


    private lateinit var activityReceiver: ActivityReceiver

    override fun updateHistory() {
        if (children[FRAGMENT_HISTORY] != null) {
            children[FRAGMENT_HISTORY]?.refresh()
        }
    }

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
        activityReceiverFilter.addAction(Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE)
        activityReceiverFilter.addAction(Constants.Broadcasts.HISTORY_RETRIEVED)
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
