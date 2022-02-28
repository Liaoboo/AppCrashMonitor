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

    override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
        setTopActivity(activity)
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

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityList.remove(activity)
    }

    private fun setTopActivity(activity: Activity) {
        if (!mActivityList.isEmpty() && mActivityList.first == activity) {
            return
        }
        if (mActivityList.contains(activity)) {
            if (mActivityList.first != activity) {
                mActivityList.remove(activity)
                mActivityList.addFirst(activity)
            }
        } else {
            mActivityList.addFirst(activity)
        }
    }

    private fun getSameActivity(currentActivity: Activity): Activity? {
        for (activity in mActivityList) {
            if (activity.javaClass.simpleName == currentActivity.javaClass.simpleName) {
                return activity
            }
        }
        return null
    }

    fun getTopActivity(): Activity? {
        if (!mActivityList.isEmpty()) {
            return mActivityList.first
        }
        return null
    }

    fun getActivityList(): LinkedList<Activity> {
        return mActivityList
    }
}