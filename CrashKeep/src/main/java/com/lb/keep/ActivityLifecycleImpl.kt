package com.lb.keep

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

/**
 * Created by Liaobo
 */
class ActivityLifecycleImpl(sApplication: Application) :
    Application.ActivityLifecycleCallbacks {

    private val mActivityList = LinkedList<Activity>()

    init {
        sApplication.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        setTopActivity(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        setTopActivity(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        setTopActivity(activity)
    }

    override fun onActivityPaused(activity: Activity) { /**/
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) { /**/
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityList.remove(activity)
    }

    private fun setTopActivity(activity: Activity) {
        if (mActivityList.contains(activity)) {
            if (mActivityList.last != activity) {
                mActivityList.remove(activity)
                mActivityList.addLast(activity)
            }
        } else {
            mActivityList.addLast(activity)
        }
    }

    fun getTopActivity(): Activity? {
        if (!mActivityList.isEmpty()) {
            return mActivityList.last
        }
        return null
    }

    fun getActivityList(): LinkedList<Activity> {
        return mActivityList
    }
}