package org.codroid.editor.utils

import kotlinx.coroutines.*
import kotlin.time.Duration

/**
 * Convenience for creating a [TimerTask], GlobalScope is default for coroutine scope.
 */
@OptIn(DelicateCoroutinesApi::class)
object Timer {

    /**
     * Create a [TimerTask].
     */
    fun create(
        duration: Duration,
        doing: suspend CoroutineScope.() -> Unit,
        context: CoroutineScope = GlobalScope
    ): TimerTask = TimerTask(duration, doing, context)

    /**
     * Create a [TimerTask] with milliseconds.l
     */
    fun create(
        duration: Long,
        doing: suspend CoroutineScope.() -> Unit,
        context: CoroutineScope = GlobalScope
    ): TimerTask = TimerTask(Duration.parse("${duration}ms"), doing, context)
}

/**
 * Delay in executing tasks, and it is cancelable, relay on coroutine context.
 *
 * @property mDuration The task will be executed in [mDuration].
 * @property mDoing The task you want to perform.
 * @property mContext A coroutine context
 */
@OptIn(DelicateCoroutinesApi::class)
class TimerTask(
    private val mDuration: Duration,
    private val mDoing: suspend CoroutineScope.() -> Unit,
    private val mContext: CoroutineScope
) {

    private val mThreadContext = newSingleThreadContext("TimerTask Thread: ${mDoing.hashCode()}")

    private var mJob: Job? = null

    /**
     * Start the task, it will run on a none-main thread.
     */
    fun start() {
        if (mJob == null) {
            mJob = mContext.launch(mThreadContext) {
                delay(mDuration)
                mDoing()
            }
        }
    }

    /**
     * Cancel the task. And it can be started again.
     */
    fun cancel() {
        mJob?.cancel()
        mJob = null
    }
}