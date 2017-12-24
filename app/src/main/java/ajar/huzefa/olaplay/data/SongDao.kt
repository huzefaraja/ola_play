package ajar.huzefa.olaplay.data

import android.arch.persistence.room.*


@Dao
interface SongDao {

    @Query("SELECT * FROM song")
    fun all(): List<Song>

    @Query("SELECT * FROM song WHERE title LIKE :query OR artist LIKE :query")
    fun search(query: String): List<Song>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg songs: Song): Array<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(songs: Song): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg songs: Song)

    @Delete
    fun delete(song: Song)
}