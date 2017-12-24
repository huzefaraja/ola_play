package ajar.huzefa.olaplay.fragments

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.adapters.SongAdapter
import ajar.huzefa.olaplay.data.Data
import ajar.huzefa.olaplay.data.Song
import ajar.huzefa.olaplay.playback.PlaybackManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recycler_view.*


/**
 *
 * Fragment to Display Songs. It uses filtering on the set of songs to decide what to display.
 *
 * It implements the ajar.huzefa.olaplay.fragments.LovedChild interface
 *
 */

class SongsFragment : Fragment(), LovedChild, TextWatcher {
    override fun afterTextChanged(s: Editable?) {
        filter = s?.toString().orEmpty().toLowerCase()
        refresh()
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun getFragmentType(): Int {
        return typeOfFragment
    }

    override fun asFragment(): Fragment {
        return this
    }

    private var backgroundColor: Int = -1
    private var typeOfFragment = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        log("SongsFragment onCreate()")
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            if (arguments.containsKey(ARG_TYPE_OF_FRAGMENT)) {
                typeOfFragment = arguments[ARG_TYPE_OF_FRAGMENT] as Int
                log("$ARG_TYPE_OF_FRAGMENT = $typeOfFragment")
            }
        }
        if (typeOfFragment > 0) {
            lovingParent?.theResponsibleParent(this)
        }
    }

    private lateinit var songAdapter: SongAdapter

    @Synchronized
    private fun createAdapter() {
        log("createAdapter()")
        if (typeOfFragment == FRAGMENT_MOST_PLAYED_SONGS) songAdapter = SongAdapter(context, R.layout.song_row_most_played)
        else songAdapter = SongAdapter(context, R.layout.song_row)
    }

    @Synchronized
    private fun getAdapter(): SongAdapter {
        log("getAdapter()")
        return try {
            songAdapter
        } catch (e: UninitializedPropertyAccessException) {
            createAdapter()
            getAdapter()
        }
    }

    @Synchronized
    private fun setAdapter(): Boolean {
        log("SongsFragment setAdapter()")
        return try {
            log("Setting adapter...")
            recyclerView?.layoutManager = LinearLayoutManager(context)
            recyclerView?.adapter = getAdapter()
            log("Adapter set!")
            true
        } catch (e: UninitializedPropertyAccessException) {
            log("recyclerView is not available!")
            false

        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is LovingParent) {
            lovingParent = context
        } else {
            // no loving parent
            // an orphan fragment?
        }
    }


    override fun onDetach() {
        super.onDetach()
        lovingParent = null
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        log("SongsFragment onCreateView()")
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        log("SongsFragment onViewCreated()")
        super.onViewCreated(view, savedInstanceState)
        if (typeOfFragment == FRAGMENT_QUEUE)
            if (view != null) view.setBackgroundColor(Color.argb(235, 255, 255, 255))
            else log("That view was null")
        else if (typeOfFragment == FRAGMENT_FILTERED_SONGS) {
            editTextSearch?.visibility = View.VISIBLE
            editTextSearch?.addTextChangedListener(this)
        }
        if (recyclerView != null) {
            recyclerView?.layoutManager = LinearLayoutManager(context)

            refresh()
        }
    }


    companion object {

        @JvmField
        val ARG_TYPE_OF_FRAGMENT = "type_of_fragment"


        @JvmField
        val FRAGMENT_ALL_SONGS = 4
        @JvmField
        val FRAGMENT_DOWNLOADED_SONGS = 8
        @JvmField
        val FRAGMENT_QUEUE = 15
        @JvmField
        val FRAGMENT_LIKED_SONGS = 16
        @JvmField
        val FRAGMENT_FILTERED_SONGS = 23
        @JvmField
        val FRAGMENT_MOST_PLAYED_SONGS = 42

        @JvmStatic
        fun newInstance(typeOfFragment: Int): SongsFragment {
            log("SongsFragment newInstance()")
            val fragment = SongsFragment()
            val args = Bundle()
            args.putInt(ARG_TYPE_OF_FRAGMENT, typeOfFragment)
            fragment.arguments = args
            return fragment
        }

    }


    @Synchronized
    override fun refresh() {
        log("SongsFragment refresh()")
        if (setAdapter()) {
            songAdapter.setSongs(getSongs())
            songAdapter.notifyDataSetChanged()
        }
    }

    var filter: String = ""


    /**
     *
     * Filters the song collection according to the typeOfFragment
     *
     */
    private fun getSongs(): Collection<Song> {
        log("SongsFragment getSongs() $typeOfFragment")
        return when (typeOfFragment) {
            FRAGMENT_ALL_SONGS -> Data.get(context).songs
            FRAGMENT_DOWNLOADED_SONGS -> Data.get(context).songs.filter { it.downloaded == Song.DOWNLOADED }
            FRAGMENT_LIKED_SONGS -> Data.get(context).songs.filter { it.liked == Song.LIKED }
            FRAGMENT_FILTERED_SONGS ->
                Data.get(context).songs.filter { it.title.orEmpty().toLowerCase().contains(filter) or it.artist.orEmpty().toLowerCase().contains(filter) }
            FRAGMENT_MOST_PLAYED_SONGS -> Data.get(context).songs.sortedByDescending { it.playCount }
            FRAGMENT_QUEUE -> PlaybackManager.get(context).queue
            else -> Data.get(context).songs
        }

    }

    private var lovingParent: LovingParent? = null
}
