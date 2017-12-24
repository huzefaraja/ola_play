package ajar.huzefa.olaplay.data

import android.arch.persistence.room.*



@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun all(): List<History>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg Histories: History): Array<Long>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(Histories: History): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(vararg Histories: History)

@Delete
fun delete(History: History)
}