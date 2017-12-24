package ajar.huzefa.olaplay.playback

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.utility.Constants
import android.content.Context
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.AppCompatSeekBar
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.squareup.picasso.Picasso


/**
 * This view takes a player control and assigns transition names to it.
 * It also makes them clickable
 * and calls the required function (like android.widget.MediaController does)
 */
class PlayerView(val view: android.view.View, val context: Context) : View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    val handler: Handler?

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (seekBar != this.seekBar) seekBar?.setOnSeekBarChangeListener(this)
        else {
            if (fromUser) {
                val player = PlaybackManager.get(context).player
                try {
                    val currentPosition = progress * player.duration / 100
                    player.seekTo(currentPosition)
                    elapsedTextView?.text = readableMilliseconds(currentPosition)
                    durationTextView?.text = readableMilliseconds(player.duration)
                    seek()
                } catch (e: Exception) {
                    log(e)
                    e.printStackTrace()
                }

            } else {

            }
            tracking = false
        }
    }

    var tracking = false

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != this.seekBar) seekBar?.setOnSeekBarChangeListener(null)
        tracking = true
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != this.seekBar) seekBar?.setOnSeekBarChangeListener(null)
        tracking = false
    }

    override fun onClick(v: View?) {
        println(v)
        if (v != null) when (v) {
            artImageView -> view.performClick()
            titleTextView -> view.performClick()
            artistTextView -> view.performClick()
            else -> {
                try {
                    when (v) {
                        playPauseButton -> PlaybackManager.get(context).playOrPause()
                        nextButton -> PlaybackManager.get(context).next()
                        previousButton -> PlaybackManager.get(context).previous()
                        forwardButton -> PlaybackManager.get(context).fastForward()
                        rewindButton -> PlaybackManager.get(context).rewind()
                    }
                } catch (e: Exception) {
                    log(e)
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     *
     * A runnable to move the seekbar
     *
     */
    val seek = Runnable {
        val seekBar = this.seekBar
        if (seekBar != null) {
            val player = PlaybackManager.get(context).player
            try {
                if (!tracking) {
                    seekBar.progress = player.currentPosition * 100 / player.duration
                    elapsedTextView?.text = readableMilliseconds(player.currentPosition)
                    durationTextView?.text = readableMilliseconds(player.duration)
                    seek()
                }
            } catch (e: Exception) {
                log(e)

                e.printStackTrace()
            }
        }
    }

    /**
     * Converts milliseconds into minutes & seconds
     */

    fun readableMilliseconds(milliseconds: Int): String {
        val seconds = milliseconds / 1000
        if (seconds > 59) {
            val minutes = seconds / 60
            val seconds = seconds % 60
            return "$minutes:${pad(seconds)}"
        } else {
            return "0:${pad(seconds)}"
        }
    }

    fun pad(seconds: Int): String {
        if (seconds > 9) return seconds.toString()
        else return "0$seconds"
    }

    fun seek() {
        val seekBar = this.seekBar
        if (seekBar != null) {
            handler?.postDelayed(seek, 1000)
        }

    }

    fun seekNow() {
        handler?.post(seek)
    }


    val playPauseButton: ImageButton? = view.findViewById(R.id.buttonPlayPause)
    val nextButton: ImageButton? = view.findViewById(R.id.buttonNext)
    val previousButton: ImageButton? = view.findViewById(R.id.buttonPrevious)
    val forwardButton: ImageButton? = view.findViewById(R.id.buttonForward)
    val rewindButton: ImageButton? = view.findViewById(R.id.buttonRewind)
    val artImageView: ImageView? = view.findViewById(R.id.imageViewArt)
    val titleTextView: TextView? = view.findViewById(R.id.textViewTitle)
    val artistTextView: TextView? = view.findViewById(R.id.textViewArtist)
    val durationTextView: TextView? = view.findViewById(R.id.textViewDuration)
    val elapsedTextView: TextView? = view.findViewById(R.id.textViewElapsed)
    val seekBar: AppCompatSeekBar? = view.findViewById(R.id.seekBar)
    val artImageView2: ImageView? = view.findViewById(R.id.imageViewArt2)

    init {

        setIcons()
        setListeners()
        setTransitionNames()
        handler = try {
            Handler()
        } catch (e: Exception) {
            null
        }
        val seekBar = seekBar
        if (seekBar != null) {
            seekBar.setOnSeekBarChangeListener(this)

        }

    }

    fun setIcons() {
        try {
            log("updating icons")
            if (PlaybackManager.get(context).player.isPlaying) {
                playPauseButton?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause))
            } else {
                playPauseButton?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play))
            }

        } catch (e: Exception) {
            playPauseButton?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause))
        }
    }

    fun setListeners() {
        playPauseButton?.setOnClickListener(this)
        titleTextView?.setOnClickListener(this)
        artistTextView?.setOnClickListener(this)
        artImageView?.setOnClickListener(this)
        nextButton?.setOnClickListener(this)
        previousButton?.setOnClickListener(this)
        forwardButton?.setOnClickListener(this)
        rewindButton?.setOnClickListener(this)
        seekBar?.setOnSeekBarChangeListener(this)
    }


    private fun setTransitionNames() {
        if (playPauseButton != null) {
            ViewCompat.setTransitionName((playPauseButton), "playPauseButton")
            ViewCompat.setElevation(playPauseButton, 8f)
        }
        if (titleTextView != null) ViewCompat.setTransitionName((titleTextView), "titleTextView")
        if (artistTextView != null) ViewCompat.setTransitionName((artistTextView), "artistTextView")
        if (artImageView != null) ViewCompat.setTransitionName((artImageView), "artImageView")
        if (nextButton != null) ViewCompat.setTransitionName((nextButton), "nextButton")
        if (previousButton != null) ViewCompat.setTransitionName((previousButton), "previousButton")
        if (forwardButton != null) ViewCompat.setTransitionName((forwardButton), "forwardButton")
        if (rewindButton != null) ViewCompat.setTransitionName((rewindButton), "rewindButton")
    }

    fun setSong(song: Song?) {
        if (song != null) {
            log("Setting song $song to UI")
            this.titleTextView?.text = song.title
            this.artistTextView?.text = song.artist
            val pic = Picasso.with(context.applicationContext)
                    .load(song.redirectedCoverUrl)
                    .placeholder(R.drawable.artwork_placeholder)
            if (artImageView != null)
                pic.into(artImageView)
            if (artImageView2 != null)
                pic.into(artImageView2)

        }
        setIcons()
        seekNow()
    }

    fun event(event: String) {
        when (event) {
            Constants.Broadcasts.PLAY -> play()
            Constants.Broadcasts.PAUSE -> pause()
        }
    }

    fun play() {
        playPauseButton?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pause))
        seekNow()
    }

    fun pause() {
        playPauseButton?.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play))
    }


}