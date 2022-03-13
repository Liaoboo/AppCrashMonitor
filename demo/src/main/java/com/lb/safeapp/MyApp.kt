package com.lb.safeapp

import android.app.Activity
import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.lb.keep.CrashMonitor
import com.lb.keep.IHandlerException

/**
 * Created by Liaobo
 */
class MyApp : Application() {
    companion object {
        var topAct: Activity? = null
    }

    init {
        CrashMonitor.INSTANCE.init(this, object : IHandlerException {

            override fun handlerException(e: Throwable): Boolean {

                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        val view = topAct?.window?.decorView?.findViewById<ViewGroup>(
                            android.R.id.content
                        )
                        if (view != null) {
                            val snackBar = Snackbar.make(view, "程序运行出错啦！", Snackbar.LENGTH_LONG)
                            snackBar.setAction("重启应用") {
                                CrashMonitor.INSTANCE.relaunchApp(this@MyApp, e)
                            }
                            snackBar.setActionTextColor(Color.RED)
                            snackBar.show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MyApp, "程序运行出错啦！", Toast.LENGTH_SHORT).show()
                    }

                }, 260)

                //可上传错误日志等
                return false
            }

            override fun notFindOrFail(throwable: Throwable): Boolean {
                return super.notFindOrFail(throwable)
                // do something
            }


        }).isShowLog(true)
    }
}