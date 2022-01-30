package com.lb.keep

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlin.system.exitProcess


/** App crash 防护
 * Created by Liaobo
 */
enum class CrashMonitor {
    INSTANCE;

    private var isShowLog: Boolean = true
    private lateinit var lifecycleImpl: ActivityLifecycleImpl
    private lateinit var context: Context

    fun init(app: Application?, handlerException: IHandlerException?): CrashMonitor {
        if (app != null) {
            lifecycleImpl = ActivityLifecycleImpl(app)
            context = app
            safeMode(handlerException)
        }

        return INSTANCE
    }

    fun isShowLog(isShowLog: Boolean) {
        this.isShowLog = isShowLog
    }

    /**
     * 开启保护模式
     */
    private fun safeMode(iHandlerException: IHandlerException?) {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Exception) {
                    log("safeMode() Catch exception", e)
                    var hasDone = true
                    try {
                        if (iHandlerException == null || !iHandlerException.handlerException(e)) {
                            hasDone = finishExceptionActivity(e)
                        }
                    } catch (e: Exception) {
                        hasDone = false
                        log("safeMode() Catch exception() -> Exception() :${e.message}")
                    } finally {
                        if (!hasDone) {
                            if (iHandlerException != null && iHandlerException.notFindOrFail(e)) {
                                break
                            }
                            try {
                                relaunchApp()
                            } catch (e: Exception) {
                                log("safeMode() relaunchApp() -> Exception() :${e.message}")
                                killProcess()
                            }

                        }
                    }
                }
            }
        }
    }

    private fun finishExceptionActivity(e: Throwable): Boolean {
        val elements = e.stackTrace
        var isFindUI = false
        for (element in elements) {
            val cls = Class.forName(elements[0].className)
            if (Activity::class.java.isAssignableFrom(cls)) {
                log("finishExceptionActivity() finish activity name: ${elements[0].className}")
                val activities: List<Activity> = lifecycleImpl.getActivityList()
                for (activity in activities) {
                    if (activity.javaClass == cls) {
                        if (!cls.isAnnotationPresent(CrashKeepAlive::class.java)) {
                            activity.finish()
                            activity.overridePendingTransition(0, 0)
                        }
                        isFindUI = true
                    }
                }
                break
            }
        }

        if (!isFindUI) {
            val activity = lifecycleImpl.getTopActivity()
            if (activity != null) {
                if (!activity.javaClass.isAnnotationPresent(CrashKeepAlive::class.java)) {
                    activity.finish()
                }
            }
            isFindUI = true
        }

        return isFindUI
    }

    private fun killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }

    private fun relaunchApp() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent =
                context.packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
            intent?.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            context.startActivity(intent)
        }, 100)
    }

    private fun log(msg: String, e: Exception? = null) {
        if (!isShowLog) {
            return
        }
        Log.e(javaClass.simpleName, msg, e)
    }
}