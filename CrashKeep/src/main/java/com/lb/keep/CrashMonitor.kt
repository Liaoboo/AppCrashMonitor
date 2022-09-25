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

    fun init(app: Application?, handlerException: IHandlerException?): CrashMonitor {
        if (app != null) {
            lifecycleImpl = ActivityLifecycleImpl(app)
            safeMode(app, handlerException)
        }

        return INSTANCE
    }

    fun isShowLog(isShowLog: Boolean) {
        this.isShowLog = isShowLog
    }

    /**
     * 开启保护模式
     */
    private fun safeMode(context: Context, iHandlerException: IHandlerException?) {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Exception) {
                    log("safeMode() Catch exception", e)
                    var hasDone = true
                    try {
                        if (iHandlerException == null || !iHandlerException.handlerException(e)) {
                            hasDone = finishStackTraceExceptionActivity(e)
                            if (!hasDone) {
                                hasDone = finishInitExceptionActivity(e)
                            }
                        }
                    } catch (e: Exception) {
                        hasDone = false
                        log("safeMode() Catch exception() -> Exception() :${e.message}")
                    } finally {
                        log("hasDone:$hasDone")
                        if (!hasDone) {
                            if (iHandlerException != null && iHandlerException.notFindOrFail(e)) {
                                break
                            }

                            relaunchApp(context, e)
                        }
                    }
                }
            }
        }
    }

    private fun finishStackTraceExceptionActivity(throwable: Throwable): Boolean {
        val elements = throwable.stackTrace
        for (element in elements) {
            val cls = Class.forName(element.className)
            if (Activity::class.java.isAssignableFrom(cls)) {
                log("finishStackTraceExceptionActivity() finish activity name: ${element.className}")
                val activities: List<Activity> = lifecycleImpl.getActivityList()
                for (activity in activities) {
                    if (activity.javaClass == cls) {
                        if (!cls.isAnnotationPresent(CrashKeepAlive::class.java)) {
                            activity.finish()
                            activity.overridePendingTransition(0, 0)
                        }
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun finishInitExceptionActivity(throwable: Throwable): Boolean {
        var isInitError = false
        val elements = throwable.stackTrace
        for (element in elements) {
            if (element.fileName == "ActivityThread.java" && element.methodName == "performLaunchActivity") {
                isInitError = true
                break
            }
        }

        if (isInitError && throwable.message?.startsWith("Unable to instantiate activity") == true) {
            goBack()
            return true
        }

        if (isInitError && throwable.message?.startsWith("Unable to start activity") == true) {
            throwable.message?.let {
                val start = it.indexOf("ComponentInfo")
                if (start != -1) {
                    val errorComponentName = it.substring(start, it.indexOf(": "))
                    for (activity in lifecycleImpl.getActivityList()) {
                        if (errorComponentName == activity.componentName.toString()) {
                            activity.finish()
                            activity.overridePendingTransition(0, 0)
                            return true
                        }
                    }

                }

            }
            return false
        }

        finishTopActivity()
        return true
    }

    private fun goBack() {
        val activity = lifecycleImpl.getTopActivity()
        if (activity != null) {
            val intent = Intent(activity, activity.javaClass)
            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
            activity.startActivity(intent)
        }
    }

    private fun finishTopActivity() {
        val activity = lifecycleImpl.getTopActivity()
        if (activity != null) {
            if (!activity.javaClass.isAnnotationPresent(CrashKeepAlive::class.java)) {
                activity.finish()
                activity.overridePendingTransition(0, 0)
            }
        }
    }

    private fun killProcess() {
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }

    fun relaunchApp(context: Context, throwable: Throwable?) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val intent =
                    context.packageManager.getLaunchIntentForPackage(context.applicationContext.packageName)
                intent?.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TOP
                            or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                if (throwable != null) {
                    throw Exception(throwable)
                } else {
                    killProcess()
                }
            }

        }, 100)
    }

    private fun log(msg: String, e: Exception? = null) {
        if (!isShowLog) {
            return
        }
        Log.e(javaClass.simpleName, msg, e)
    }
}