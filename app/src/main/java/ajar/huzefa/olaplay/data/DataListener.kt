package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.utility.Constants
import android.content.Context
import android.content.Intent


class DataListener(val context: Context) : Entity.OnEntitiesInsertedListener, Entity.OnEntitiesUpdatedListener, Entity.OnEntitiesDeletedListener, Entity.OnEntitiesQueriedListener {

    override fun onEntitiesInserted(taskId: Int, entities: Array<out Entity?>?) {
        OlaPlay.log("onEntitiesInserted($taskId)")
        when (taskId) {
            Constants.Tasks.NEW_HISTORY_ITEM -> {
                OlaPlay.log(entities)
                if (entities != null) {
                    OlaPlay.log(entities)
                    try {
                        val entities = entities
                        entities.filter { it != null }.forEach { Data.get(context).history.add(it as History) }
                    } catch (e: Exception) {
                        context.sendBroadcast(Intent(Constants.Broadcasts.NEW_HISTORY_ITEMS_AVAILABLE))
                    } catch (e: Exception) {
                        OlaPlay.log("Error in NEW_HISTORY_ITEM: $e")
                        e.printStackTrace()
                    }
                }
            }
            else -> OlaPlay.log("Unknown request!")
        }

    }

    override fun onEntitiesUpdated(taskId: Int, entities: Array<out Entity?>?) {
    }

    override fun onEntitiesDeleted(taskId: Int, entities: Array<out Entity?>?) {
    }

    override fun onEntitiesQueried(taskId: Int, type: Class<*>, result: List<*>?) {
    }

}