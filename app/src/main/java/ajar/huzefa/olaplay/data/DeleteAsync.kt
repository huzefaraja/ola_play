package ajar.huzefa.olaplay.data

import ajar.huzefa.olaplay.utility.Constants
import android.content.Context
import android.content.Intent
import android.os.AsyncTask


class DeleteAsync : AsyncTask<Entity, Unit, Array<out Entity?>> {
    val context: Context
    val listener: Entity.OnEntitiesDeletedListener?
    val taskId: Int
    val action: String?

    constructor(context: Context, taskId: Int) : this(context, null, null, taskId)
    constructor(context: Context, action: String?, taskId: Int) : this(context, action, null, taskId)
    constructor(context: Context, listener: Entity.OnEntitiesDeletedListener?, taskId: Int) : this(context, null, listener, taskId)
    constructor(context: Context, action: String?, listener: Entity.OnEntitiesDeletedListener?, taskId: Int) : super() {
        this.context = context.applicationContext
        this.action = action
        this.listener = listener
        this.taskId = taskId

    }

    override fun doInBackground(vararg entities: Entity?): Array<out Entity?> {
        if (entities.isNotEmpty()) {
            for (entity in entities) {
                if (entity != null) {
                    entity.delete(context)
                }
            }
        }
        return entities
    }

    override fun onPostExecute(result: Array<out Entity?>?) {
        super.onPostExecute(result)
        if (action != null) {
            context.sendBroadcast(Intent(action).putExtra(Constants.Extras.ASYNC_TASK_ID, taskId))
        }
        if (listener != null) {
            listener.onEntitiesDeleted(taskId, result)
        }
    }
}