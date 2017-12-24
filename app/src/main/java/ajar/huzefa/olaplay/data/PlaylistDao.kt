package ajar.huzefa.olaplay.data

import android.arch.persistence.room.*


/**
 * This item is not currently in use
 */
@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    fun all(): List<Playlist>

    @Query("SELECT * FROM playlist WHERE id=(:id)")
    fun byId(id: Long): List<Playlist>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg Histories: Playlist): Array<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(Histories: Playlist): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg Histories: Playlist)

    @Delete
    fun delete(Playlist: Playlist)
}