package ajar.huzefa.olaplay.fragments

import ajar.huzefa.olaplay.OlaPlay.log
import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.adapters.HistoryAdapter
import ajar.huzefa.olaplay.data.Data
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_recycler_view.*

/**
 * Fragment to Display History.
 *
 * It implements the ajar.huzefa.olaplay.fragments.LovedChild interface
 *
 */
class HistoryFragment : Fragment(), LovedChild {

    override fun getFragmentType(): Int {
        return FRAGMENT_HISTORY
    }

    override fun asFragment(): Fragment {
        return this
    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_recycler_view, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (recyclerView != null) {
            recyclerView?.layoutManager = LinearLayoutManager(context)
            refresh()
        }
    }


    companion object {
        @JvmField
        val FRAGMENT_HISTORY = 108

        @JvmStatic
        fun newInstance(): HistoryFragment {
            return HistoryFragment()
        }
    }

    private lateinit var historyAdapter: HistoryAdapter

    var lovingParent: LovingParent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lovingParent?.theResponsibleParent(this)
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


    @Synchronized
    private fun createAdapter() {
        log("createAdapter()")
        if (context != null)
            historyAdapter = HistoryAdapter(context)
    }

    @Synchronized
    private fun getAdapter(): HistoryAdapter? {
        log("getAdapter()")
        return try {
            historyAdapter
        } catch (e: UninitializedPropertyAccessException) {
            createAdapter()
            getAdapter()
        }
    }

    @Synchronized
    private fun setAdapter(): Boolean {
        log("HistoryFragment setAdapter()")
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

    @Synchronized
    override fun refresh() {
        if (context != null) {
            log("HistoryFragment refresh()")
            if (setAdapter()) {
                try {
                    historyAdapter.setList(Data.get(context).history)
                    historyAdapter.notifyDataSetChanged()
                } catch (e: UninitializedPropertyAccessException) {
                    createAdapter()
                    setAdapter()
                }
            }
        }
    }
}
