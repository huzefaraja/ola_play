package ajar.huzefa.olaplay.data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context


/**
 * This item is not currently in use
 */
@Entity
class Playlist : ajar.huzefa.olaplay.data.Entity {
    override fun insert(context: Context): Long {
        this.id = Data.base(context).playlistDao().insert(this)
        return this.id
    }

    override fun update(context: Context) {
        Data.base(context).playlistDao().update(this)
    }

    override fun delete(context: Context) {
        Data.base(context).playlistDao().delete(this)
    }


    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "songs")
    var songs: Long = 0

    companion object {
        @JvmStatic
        fun all(context: Context) = Data.base(context).playlistDao().all()
    }

    override fun toString(): String {
        return "\t$id\t${title}\t$songs"
    }
}