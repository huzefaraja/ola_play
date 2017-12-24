package ajar.huzefa.olaplay.data

import android.content.Context


interface Entity {

    fun insert(context: Context): Long
    fun update(context: Context)
    fun delete(context: Context)

    interface OnEntitiesInsertedListener {
        fun onEntitiesInserted(taskId: Int, entities: Array<out Entity?>?)
    }

    interface OnEntitiesUpdatedListener {
        fun onEntitiesUpdated(taskId: Int, entities: Array<out Entity?>?)
    }

    interface OnEntitiesDeletedListener {
        fun onEntitiesDeleted(taskId: Int, entities: Array<out Entity?>?)
    }

    interface OnEntitiesQueriedListener {
        fun onEntitiesQueried(taskId: Int, type: Class<*>, result: List<*>?)
    }

}