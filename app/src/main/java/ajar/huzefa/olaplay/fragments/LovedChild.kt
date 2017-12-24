package ajar.huzefa.olaplay.fragments

import android.support.v4.app.Fragment


/**
 *
 * if a Fragment is a LovedChild, its parent can tell it when its time to get ready.
 *
 */
interface LovedChild {

    fun getFragmentType(): Int
    fun asFragment(): Fragment
    fun refresh()

}