package ajar.huzefa.olaplay.services

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.data.*
import ajar.huzefa.olaplay.playback.PlaybackManager
import ajar.huzefa.olaplay.receivers.OfflineDataReadyServiceReceiver
import ajar.huzefa.olaplay.receivers.OnlineDataReadyServiceReceiver
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE
import ajar.huzefa.olaplay.utility.Constants.Broadcasts.NEW_SONGS_AVAILABLE
import ajar.huzefa.olaplay.utility.Constants.Broadcasts.NO_NEW_SONGS_AVAILABLE
import ajar.huzefa.olaplay.utility.Constants.Broadcasts.SONGS_LOADED_FROM_DB
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_CLEAR_DATA_HISTORY
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_CLEAR_DATA_SONGS
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_GET_HISTORY_FROM_DATABASE
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_GET_NEW_SONGS_FROM_JSON_ARRAY
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_GET_REDIRECTED_URLS
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_GET_SONGS_FROM_DATABASE
import ajar.huzefa.olaplay.utility.Constants.Tasks.TASK_INSERT_SONGS_INTO_DATABASE
import ajar.huzefa.olaplay.utility.FetchUrlAfterRedirection
import ajar.huzefa.olaplay.utility.GetNewSongsFromJSONArray
import ajar.huzefa.olaplay.utility.OnAsyncTaskCompletionListener
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.IBinder
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import org.json.JSONArray

/**
 *
 * This service manages most of the data related activiites
 * such as get songs in database,
 * get songs from the api and put them in the database,
 * get history from database, etc.
 *
 */

class DataService : Service(),
        Response.Listener<JSONArray>, Response.ErrorListener,
        ajar.huzefa.olaplay.data.Entity.OnEntitiesInsertedListener,
        ajar.huzefa.olaplay.data.Entity.OnEntitiesQueriedListener,
        ajar.huzefa.olaplay.data.Entity.OnEntitiesUpdatedListener,
        ajar.huzefa.olaplay.data.Entity.OnEntitiesDeletedListener, OnAsyncTaskCompletionListener {
    override fun OnAsyncTaskCompleted(taskId: Int, output: Any?) {
        log("OnAsyncTaskCompleted($taskId)")
        when (taskId) {
            TASK_GET_NEW_SONGS_FROM_JSON_ARRAY -> {
                if (output != null) {
                    try {
                        val newSongs = output as ArrayList<Song>
                        taskFetchUrlAfterRedirection.execute(*newSongs.toTypedArray())
                    } catch (e: Exception) {
                        log("Error in TASK_GET_NEW_SONGS_FROM_JSON_ARRAY: $e")
                        e.printStackTrace()
                    }
                }
            }
            TASK_GET_REDIRECTED_URLS -> {
                if (output != null) {
                    try {
                        val songs = output as Array<Song>
                        taskInsertSongsIntoDatabase = InsertAsync(this, this, TASK_INSERT_SONGS_INTO_DATABASE)
                        taskInsertSongsIntoDatabase.execute(*songs)
                    } catch (e: Exception) {
                        log("Error in TASK_GET_REDIRECTED_URLS: $e")
                        e.printStackTrace()
                    }
                }
            }

            else -> log("Unknown request!")
        }
    }

    override fun onEntitiesQueried(taskId: Int, type: Class<*>, result: List<*>?) {
        log("onEntitiesQueried($taskId)")
        when (taskId) {
            TASK_GET_SONGS_FROM_DATABASE -> {
                if (result != null && type == Song::class.java) {
                    log(result)
                    val songs = Data.get(this).songs
                    if (result.isNotEmpty()) {
                        val list = result as List<ajar.huzefa.olaplay.data.Song>
                        if (songs.size == 0 && list.isNotEmpty()) songs.addAll(list)
                    }
                    sendBroadcast(Intent(SONGS_LOADED_FROM_DB))
                    getSongsFromAPI()
                    //DeleteAsync(this,this,56).execute(*list.toTypedArray())
                } else {
                    log(result)
                    sendBroadcast(Intent(SONGS_LOADED_FROM_DB))
                    getSongsFromAPI()
                }
            }
            TASK_GET_HISTORY_FROM_DATABASE -> {
                log("Result $result ${result?.size}")
                if (result != null && type == History::class.java) {
                    log(result)
                    val history = Data.get(this).history
                    if (result.isNotEmpty()) {
                        val list = result as List<ajar.huzefa.olaplay.data.History>
                        if (history.size == 0 && list.isNotEmpty()) history.addAll(list)
                    }
                    sendBroadcast(Intent(NEW_HISTORY_ITEMS_AVAILABLE))
                } else {
                    log(result)
                }
                log("History after task TASK_GET_HISTORY_FROM_DATABASE ($TASK_GET_HISTORY_FROM_DATABASE): " + Data.get(this).history)
            }
            else -> log("Unknown request!")
        }

    }

    override fun onEntitiesInserted(taskId: Int, entities: Array<out Entity?>?) {
        log("onEntitiesInserted($taskId)")
        when (taskId) {
            TASK_INSERT_SONGS_INTO_DATABASE -> {
                if (entities != null) {
                    try {
                        val songs = entities as Array<Song>
                        songs.forEach { Data.get(this).songs.add(it) }
                        sendBroadcast(Intent(NEW_SONGS_AVAILABLE))
                    } catch (e: Exception) {
                        log("Error in TASK_INSERT_SONGS_INTO_DATABASE: $e")
                        e.printStackTrace()
                    }
                }
            }
            else -> log("Unknown request!")
        }

    }

    override fun onEntitiesUpdated(taskId: Int, entities: Array<out Entity?>?) {
        log("onEntitiesUpdated()")
    }

    override fun onEntitiesDeleted(taskId: Int, entities: Array<out Entity?>?) {
        log("onEntitiesDeleted()")
        when (taskId) {
            TASK_CLEAR_DATA_HISTORY -> {
                Data.get(this).history.clear()
                startService(Intent(this, DataService::class.java).setAction(Constants.Actions.GET_HISTORY))
            }
            TASK_CLEAR_DATA_SONGS -> {
                Data.get(this).songs.clear()
                PlaybackManager.get(this).queue.clear()
                startService(Intent(this, DataService::class.java).setAction(Constants.Actions.GET_SONGS))
            }
        }
    }


    private lateinit var taskGetNewSongsFromJSONArray: GetNewSongsFromJSONArray
    private lateinit var taskFetchUrlAfterRedirection: FetchUrlAfterRedirection
    private lateinit var taskGetSongsFromDatabase: QueryAsync
    private lateinit var taskInsertSongsIntoDatabase: InsertAsync

    private lateinit var offlineDataReadyServiceReceiver: OfflineDataReadyServiceReceiver
    private val offlineDataReadyServiceReceiverFilter = IntentFilter(Constants.Broadcasts.LOAD_SONGS_FROM_API)

    private lateinit var onlineDataReadyServiceReceiver: OnlineDataReadyServiceReceiver
    private val onlineDataReadyServiceReceiverFilter = IntentFilter(Constants.Broadcasts.SONGS_REQUESTED)

    override fun onCreate() {
        super.onCreate()
        log("Data service onCreate...")
        offlineDataReadyServiceReceiver = OfflineDataReadyServiceReceiver(this)
        onlineDataReadyServiceReceiver = OnlineDataReadyServiceReceiver(this)
        registerReceiver(offlineDataReadyServiceReceiver, offlineDataReadyServiceReceiverFilter)
        registerReceiver(onlineDataReadyServiceReceiver, onlineDataReadyServiceReceiverFilter)
        log("service receivers registered...")
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(offlineDataReadyServiceReceiver)
            unregisterReceiver(onlineDataReadyServiceReceiver)
            log("service receivers unregistered...")
        } catch (e: UninitializedPropertyAccessException) {
            // never initialized
        }
        super.onDestroy()
    }

    override fun onErrorResponse(error: VolleyError?) {
        sendBroadcast(Intent(NO_NEW_SONGS_AVAILABLE))
        log("Volley responded with error! $error")
        if (error != null) {
            Toast.makeText(applicationContext, "" + error, LENGTH_SHORT).show()
            error.printStackTrace()
        }

    }


    override fun onResponse(jsonArray: JSONArray?) {
        log("API has responded")
        if (jsonArray != null) {
            log("API has responded with $jsonArray")
            taskGetNewSongsFromJSONArray = GetNewSongsFromJSONArray(Data.get(this).songs, this, TASK_GET_NEW_SONGS_FROM_JSON_ARRAY)
            taskGetNewSongsFromJSONArray.execute(jsonArray)
        }
    }

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("DataService onStartCommand()")
        if (intent != null) {
            if (intent.action == Constants.Actions.GET_SONGS) {
                log("Data service started with GET_SONGS")
                getSongs()
            }
            if (intent.action == Constants.Actions.GET_HISTORY) {
                log("Data service started with GET_HISTORY")
                getHistory()
            }
            if (intent.action == Constants.Actions.CLEAR_DATA) {
                log("Data service started with GET_HISTORY")
                DeleteAsync(this, Constants.Broadcasts.NEW_SONGS_AVAILABLE, this, TASK_CLEAR_DATA_SONGS).execute(*Data.get(this).songs.toTypedArray())
                DeleteAsync(this, Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE, this, TASK_CLEAR_DATA_HISTORY).execute(*Data.get(this).history.toTypedArray())
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun getSongs() {
        // this will need to change later
        if (Data.get(this).songsRequested) {
            Data.get(this).songsRequested = true
            getSongsFromDatabase()
        }
    }


    private fun getSongsFromDatabase() {
        log("Gettings songs from Database")
        taskGetSongsFromDatabase = QueryAsync(Song::class.java, this, this, TASK_GET_SONGS_FROM_DATABASE)
        log("Executing fetch in background")
        taskGetSongsFromDatabase.execute()
    }


    fun getHistory() {
        if (Data.get(this).historyRequested) {
            Data.get(this).historyRequested = true
            val getHistory = QueryAsync(History::class.java, this, this, TASK_GET_HISTORY_FROM_DATABASE)
            getHistory.execute()
        }
    }


    fun getSongsFromAPI() {
        if (isConnected()) {
            log("Gettings songs from API")
            val request = JsonArrayRequest(Request.Method.GET, Constants.URL.REQUEST_SONGS, null, this, this)
            taskFetchUrlAfterRedirection = FetchUrlAfterRedirection(this, TASK_GET_REDIRECTED_URLS)
            log("Requesting request queue to take care of things.")
            OlaPlay.get(this).request(request)
        } else {
            sendBroadcast(Intent(NO_NEW_SONGS_AVAILABLE))
            log("Not connected to internet. New songs may not be available!")

        }

    }

    fun putSongsInDB() {
        log("Putting songs into database")
    }

    fun isConnected(): Boolean {
        val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting
    }


}
