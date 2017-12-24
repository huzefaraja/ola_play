package ajar.huzefa.olaplay.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.util.Log

/**
 *
 * Displays Fragments with Titles
 *
 * @constructor
 * @param fragments the fragments to display
 * @param titles the titles of the fragments to display
 *
 */
class ViewPagerAdapter(fm: FragmentManager, val fragments: ArrayList<Fragment>, val titles: ArrayList<String>) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = fragments[position]
    override fun getCount() = fragments.size
    override fun getPageTitle(position: Int) = titles[position]

    init {
        Log.e("VPAVPA", "init")
    }

}