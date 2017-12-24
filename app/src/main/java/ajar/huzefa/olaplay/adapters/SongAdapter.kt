package ajar.huzefa.olaplay.adapters

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.data.Data
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.data.Song.Companion.LIKED
import ajar.huzefa.olaplay.data.Song.Companion.NOT_LIKED
import ajar.huzefa.olaplay.data.UpdateAsync
import ajar.huzefa.olaplay.playback.PlaybackManager
import ajar.huzefa.olaplay.services.DownloadService
import ajar.huzefa.olaplay.utility.Constants
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.squareup.picasso.Picasso


/**
 * Song Adapter displays songs from list in a recycler view
 */
class SongAdapter(val context: Context, val layout: Int) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    private val allSongs: ArrayList<Song> = ArrayList()
    private val visibleSongs: ArrayList<Song> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SongViewHolder {
        return SongViewHolder(LayoutInflater.from(parent?.context).inflate(layout, parent, false), context, visibleSongs)
    }

    override fun getItemCount() = visibleSongs.size


    override fun onBindViewHolder(holder: SongViewHolder?, position: Int) {
        val song = visibleSongs.get(position)
        holder?.bind(song)
    }


    class SongViewHolder(itemView: View, val context: Context, val list: ArrayList<Song>) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener, View.OnClickListener {
        override fun onClick(view: View?) {
            if (view != null) {
                when (view) {
                    itemView -> playSong()
                    contextMenu ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) itemView.performLongClick(contextMenu.x, contextMenu.pivotY)
                        else itemView.performLongClick()
                }
            }
        }

        override fun onMenuItemClick(menuItem: MenuItem?): Boolean {
            if (menuItem != null)
                when (menuItem.itemId) {
                    R.id.menu_item_play -> playSong()
                    R.id.menu_item_download -> downloadSong()
                    R.id.menu_item_add_to_queue -> addToQueue()
                    R.id.menu_item_play_next -> playNext()
                    R.id.menu_item_like_unlike -> likeUnlike()
                }
            return true
        }


        lateinit var song: Song


        override fun onCreateContextMenu(menu: ContextMenu?, view: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            if (menu != null && view != null) {
                MenuInflater(itemView.context.applicationContext).inflate(R.menu.song_menu, menu)
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    item.setOnMenuItemClickListener(this)
                    if (item.itemId == R.id.menu_item_like_unlike) {
                        try {
                            if (song.isLiked())
                                item.title = context.getString(R.string.unlike)
                            else
                                item.title = context.getString(R.string.like)
                        } catch (e: UninitializedPropertyAccessException) {
                            // no song value
                        }
                    }
                }
            }
        }


        fun bind(song: Song) {
            this.song = song
            this.title.text = song.title
            this.artists.text = song.artist
            Picasso.with(context)
                    .load(song.redirectedCoverUrl)
                    .resizeDimen(R.dimen.image_view_art_small, R.dimen.image_view_art_small)
                    .centerCrop()
                    .placeholder(R.drawable.artwork_placeholder)
                    .into(this.cover)
            this.count?.text = song.playCount.toString()
        }

        private val artists: TextView
        private val title: TextView
        private val cover: ImageView
        private val count: TextView?
        var contextMenu: ImageButton

        init {
            itemView.setOnCreateContextMenuListener(this)
            this.title = itemView.findViewById(R.id.textViewTitle)
            this.artists = itemView.findViewById(R.id.textViewArtist)
            this.cover = itemView.findViewById(R.id.imageViewArt)
            this.count = itemView.findViewById(R.id.textViewPlayCount)
            this.contextMenu = itemView.findViewById(R.id.buttonContextMenu)
            this.contextMenu.setOnClickListener(this)
            this.itemView.setOnClickListener(this)
        }

        private fun playSong() {
            try {
                log("SongAdapter playSong()")
                PlaybackManager.get(context).setNowPlaying(this.list, song)
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun playNext() {
            try {
                log("SongAdapter playNext()")
                PlaybackManager.get(context).playNext(song)
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun addToQueue() {
            try {
                log("SongAdapter playNext()")
                PlaybackManager.get(context).addToQueue(song)
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun downloadSong() {
            try {
                if (song.isDownloaded()) {
                    Toast.makeText(context, "Song already downloaded!", LENGTH_LONG).show()
                } else {
                    Data.get(context).downloads.add(song)
                    val permissionCheck = ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED)
                        context.startService(Intent(context, DownloadService::class.java).setAction(Constants.Actions.DOWNLOAD_SONG))
                    else {
                        context.sendBroadcast(Intent(Constants.Broadcasts.STORAGE_PERMISSION_REQUIRED))
                    }
                }
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun likeUnlike() {
            try {
                log("SongAdapter likeUnlike()")
                if (song.isLiked()) {
                    song.liked = NOT_LIKED
                    Data.get(context).addToHistory(song, Constants.Extras.HISTORY_UNLIKE_SONG)
                } else {
                    song.liked = LIKED
                    Data.get(context).addToHistory(song, Constants.Extras.HISTORY_LIKE_SONG)
                }
                UpdateAsync(context, Constants.Broadcasts.NEW_SONGS_AVAILABLE, -1).execute(song)
            } catch (e: UninitializedPropertyAccessException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    fun setSongs(songs: Collection<Song>) {
        allSongs.clear()
        allSongs.addAll(songs)
        visibleSongs.clear()
        visibleSongs.addAll(songs)

    }
}

