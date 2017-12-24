package ajar.huzefa.olaplay.adapters

import ajar.huzefa.olaplay.R
import ajar.huzefa.olaplay.data.History
import ajar.huzefa.olaplay.utility.Constants.Extras.HISTORY_DOWNLOAD_SONG
import ajar.huzefa.olaplay.utility.Constants.Extras.HISTORY_LIKE_SONG
import ajar.huzefa.olaplay.utility.Constants.Extras.HISTORY_PLAY_SONG
import ajar.huzefa.olaplay.utility.Constants.Extras.HISTORY_UNLIKE_SONG
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.DateFormat
import java.util.*

/**
 * History Adapter displays history from a list in a recycler view
 */
class HistoryAdapter(val context: Context) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    val history = ArrayList<History>()

    override fun onBindViewHolder(holder: HistoryViewHolder?, position: Int) {
        holder?.bind(history[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.history_row, parent, false), context)
    }

    override fun getItemCount(): Int = history.size

    fun setList(list: Collection<History>) {
        history.clear()
        history.addAll(list)
    }

    class HistoryViewHolder(itemView: View?, val context: Context) : RecyclerView.ViewHolder(itemView) {

        val imageViewExtraId: ImageView?
        val textViewTime: TextView?
        val textViewActivity: TextView?

        init {
            imageViewExtraId = itemView?.findViewById(R.id.extraImageView)
            textViewTime = itemView?.findViewById(R.id.textViewTime)
            textViewActivity = itemView?.findViewById(R.id.textViewActivity)
        }

        fun bind(history: History) {
            when (history.extraId) {
                HISTORY_PLAY_SONG -> imageViewExtraId?.setImageResource(R.drawable.play)
                HISTORY_DOWNLOAD_SONG -> imageViewExtraId?.setImageResource(R.drawable.downloaded)
                HISTORY_LIKE_SONG -> imageViewExtraId?.setImageResource(R.drawable.liked)
                HISTORY_UNLIKE_SONG -> imageViewExtraId?.setImageResource(R.drawable.unliked)
            }
            textViewActivity?.text = history.activity
            textViewTime?.text = getDate(history.timestamp)
        }

        private fun getDate(timestamp: Long): String {
            val calendar = Calendar.getInstance();
            calendar.timeInMillis = timestamp
            return DateFormat.getDateTimeInstance().format(calendar.time)

        }

    }

}