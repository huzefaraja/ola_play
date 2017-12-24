package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.utility.Constants
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context


/**
 * The history class and table had id, activity, type of item, and its timestamp
 * The default comparable sorts in descending order of timestamp
 */
@Entity
class History : ajar.huzefa.olaplay.data.Entity, Comparable<History> {
    override fun compareTo(other: History): Int {
        return timestamp.compareTo(other.timestamp) * -1
    }

    override fun insert(context: Context): Long {
        this.id = Data.base(context).historyDao().insert(this)
        return this.id
    }

    override fun update(context: Context) {
        Data.base(context).historyDao().update(this)
    }

    override fun delete(context: Context) {
        Data.base(context).historyDao().delete(this)
    }


    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "activity")
    var activity: String? = null

    @ColumnInfo(name = "extra_id")
    var extraId: Long = 0

    @ColumnInfo(name = "timestamp")
    var timestamp: Long = 0

    constructor()
    constructor(activity: String?, extraId: Long) {
        this.activity = activity
        this.extraId = extraId
        this.timestamp = System.currentTimeMillis()
    }

    constructor(song: Song, extraId: Long) {
        this.activity = when (extraId) {
            Constants.Extras.HISTORY_PLAY_SONG -> "Played ${song.title} by ${song.artist}"
            Constants.Extras.HISTORY_DOWNLOAD_SONG -> "Downloaded ${song.title} by ${song.artist}"
            Constants.Extras.HISTORY_LIKE_SONG -> "Liked ${song.title}"
            Constants.Extras.HISTORY_UNLIKE_SONG -> "Unliked ${song.title}"
            else -> "Played ${song.title} by ${song.artist}"
        }
        this.extraId = extraId
        this.timestamp = System.currentTimeMillis()
    }


    companion object {
        @JvmStatic
        fun all(context: Context) = Data.base(context).historyDao().all()
    }

    override fun toString(): String {
        return "\t$id\t${timestamp}\t$activity\t$extraId"
    }
}