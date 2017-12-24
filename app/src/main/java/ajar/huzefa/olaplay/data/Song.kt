package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.utility.Constants
import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import org.json.JSONObject



@Entity
class Song() : ajar.huzefa.olaplay.data.Entity, Comparable<Song> {
    override fun compareTo(other: Song): Int {
        return title.orEmpty().compareTo(other.title.orEmpty().toString())
    }

    override fun insert(context: Context): Long {
        this.id = Data.base(context).songDao().insert(this)
        return this.id
    }

    override fun update(context: Context) {
        Data.base(context).songDao().update(this)
    }

    override fun delete(context: Context) {
        Data.base(context).songDao().delete(this)
    }


    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "title")
    var title: String? = null

    @ColumnInfo(name = "url")
    var url: String? = null

    @ColumnInfo(name = "cover_url")
    var coverUrl: String? = null

    @ColumnInfo(name = "redirected_url")
    var redirectedUrl: String? = null

    @ColumnInfo(name = "redirected_cover_url")
    var redirectedCoverUrl: String? = null

    @ColumnInfo(name = "downloaded")
    var downloaded: String = NOT_DOWNLOADED

    @ColumnInfo(name = "liked")
    var liked: String = NOT_LIKED

    @ColumnInfo(name = "artist")
    var artist: String? = null

    @ColumnInfo(name = "hash")
    var hash: String? = null

    @ColumnInfo(name = "play_count")
    var playCount: Long = 0

    companion object {
        @JvmStatic
        fun all(context: Context) = Data.base(context).songDao().all()

        @JvmStatic
        fun fromJSON(json: JSONObject): Song {
            val song = Song()
            song.hash = json.toString()
            if (json.has(TITLE)) {
                song.title = json.getString(TITLE)
            }
            if (json.has(URL)) {
                song.url = json.getString(URL)
            }
            if (json.has(ARTISTS)) {
                val artistsJSON = json.getString(ARTISTS)
                val splitArtists = artistsJSON.split(",")
                val artists = ArrayList<String>()
                for (artistName in splitArtists) {
                    val artist = artistName.trim()
                    artists.add(artist)
                }
                song.setArtist(artists)
            }
            if (json.has(ARTWORK_URL)) {
                song.coverUrl = json.getString(ARTWORK_URL)
            }
            song.downloaded = NOT_DOWNLOADED
            song.liked = NOT_LIKED
            return song
        }


        @JvmField
        val TABLE_NAME = "song"
        @JvmField
        val TITLE = Constants.URL.SongParameters.TITLE
        @JvmField
        val URL = Constants.URL.SongParameters.URL
        @JvmField
        val REDIRECTED_URL = "redirected_" + Constants.URL.SongParameters.URL
        @JvmField
        val ARTISTS = Constants.URL.SongParameters.ARTISTS
        @JvmField
        val ARTWORK_URL = Constants.URL.SongParameters.ARTWORK_URL
        @JvmField
        val REDIRECTED_ARTWORK_URL = "redirected_" + Constants.URL.SongParameters.ARTWORK_URL
        @JvmField
        val DOWNLOADED = "downloaded"
        @JvmField
        val NOT_DOWNLOADED = "not_downloaded"
        @JvmField
        val LIKED = "liked"
        @JvmField
        val NOT_LIKED = "not_liked"
        @JvmField
        val HASH = "hash"


    }

    override fun toString(): String {
        return "\t$id\t$title by $artist ($redirectedUrl)"
    }

    fun setArtist(artists: List<String>): String {
        try {
            val artistBuilder = StringBuilder()
            if (artists.isNotEmpty()) {
                if (artists.size > 1) {
                    var i = 0
                    while (i < artists.size - 2) {
                        artistBuilder.append(artists[i])
                        artistBuilder.append(Constants.COMMA_SPACE)
                        i++
                    }
                    artistBuilder.append(artists[i++])
                    artistBuilder.append(Constants.AND)
                    artistBuilder.append(artists[i])
                } else {
                    artistBuilder.append(artists[0])
                }
            }
            artist = artistBuilder.toString()
            return artist as String
        } catch (e: UninitializedPropertyAccessException) {
            return ""
        }
    }

    fun isDownloaded(): Boolean = downloaded.equals(DOWNLOADED)
    fun isLiked(): Boolean = liked.equals(LIKED)
}