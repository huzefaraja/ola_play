package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.OlaPlay
import ajar.huzefa.olaplay.utility.Constants
import android.content.Context
import android.content.Intent
import android.os.AsyncTask


class QueryAsync : AsyncTask<Unit, Unit, List<*>> {
    val context: Context
    val listener: Entity.OnEntitiesQueriedListener?
    val type: Class<*>
    val action: String?
    val taskId: Int

    constructor(type: Class<*>, context: Context, taskId: Int) : this(type, context, null, null, taskId)
    constructor(type: Class<*>, context: Context, action: String?, taskId: Int) : this(type, context, action, null, taskId)
    constructor(type: Class<*>, context: Context, listener: Entity.OnEntitiesQueriedListener?, taskId: Int) : this(type, context, null, listener, taskId)
    constructor(type: Class<*>, context: Context, action: String?, listener: Entity.OnEntitiesQueriedListener?, taskId: Int) : super() {
        this.type = type
        this.context = context.applicationContext
        this.action = action
        this.listener = listener
        this.taskId = taskId
    }

    override fun doInBackground(vararg params: Unit?): List<*>? {
        try {
            return type.asSubclass(Entity::class.java)
                    .getMethod("all", Context::class.java)
                    .invoke(null, context) as List<*>?
        } catch (e: Exception) {
            OlaPlay.log(e)
            return null
        }

    }


    override fun onPostExecute(result: List<*>?) {
        super.onPostExecute(result)
        if (action != null) {
            context.sendBroadcast(Intent(action).putExtra(Constants.Extras.ASYNC_TASK_ID, taskId))
        }
        if (listener != null) {
            listener.onEntitiesQueried(taskId, type, result)
        }

    }
}