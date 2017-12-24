package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.services.DataService
import ajar.huzefa.olaplay.utility.Constants
import ajar.huzefa.olaplay.utility.Constants.Actions.GET_HISTORY
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import android.content.Intent
import java.util.TreeSet
import kotlin.collections.ArrayList


/**
 *
 * This class manages access to the database (returns a singleton to use with Room)
 *
 */
class Data(val context: Context, var allowMainThreadQueries: Boolean) {


    private val db: AppDatabase
    val songs: TreeSet<Song>
        @Synchronized get() = field

    val downloads: ArrayList<Song>
        @Synchronized get() = field

    val history: TreeSet<History>
        @Synchronized get() = field

    val dataListener: DataListener


    companion object {
        private var instance: Data? = null

        @Synchronized
        @JvmStatic
        fun base(context: Context): AppDatabase {
            return base(context, false)
        }

        @Synchronized
        @JvmStatic
        fun get(context: Context): Data {
            return get(context, false)

        }

        @Synchronized
        @JvmStatic
        fun get(context: Context, allowMainThreadQueries: Boolean): Data {
            if (instance == null) {
                instance = Data(context.applicationContext, allowMainThreadQueries)
            }
            return instance as Data

        }

        @Synchronized
        @JvmStatic
        fun base(context: Context, allowMainThreadQueries: Boolean): AppDatabase {
            return get(context, allowMainThreadQueries).db
        }
    }

    @Database(entities = [(Song::class), (History::class), (Playlist::class)], version = 3)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun songDao(): SongDao
        abstract fun historyDao(): HistoryDao
        abstract fun playlistDao(): PlaylistDao
    }

    init {
        if (allowMainThreadQueries) {
            db = Room.databaseBuilder(context,
                    AppDatabase::class.java, "ola_play_studios")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
        } else {
            db = Room.databaseBuilder(context,
                    AppDatabase::class.java, "ola_play_studios")
                    .fallbackToDestructiveMigration()
                    .build()
        }
        this.songs = TreeSet()
        this.downloads = ArrayList()
        this.history = TreeSet()
        this.dataListener = DataListener(context)
        context.startService(Intent(context, DataService::class.java).setAction(GET_HISTORY))
    }

    fun addToHistory(song: Song, extra: Long) {
        val history = History(song, extra)
        val insertHistory = InsertAsync(context, Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE, dataListener, Constants.Tasks.NEW_HISTORY_ITEM);
        insertHistory.execute(history)
    }

    var songsRequested = false;
    var historyRequested = false;

}