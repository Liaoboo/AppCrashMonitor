package com.lb.safeapp

import android.app.Application
import android.widget.Toast
import com.lb.keep.CrashMonitor
import com.lb.keep.IHandlerException

/**
 * Created by Liaobo
 */
class MyApp : Application() {
    init {
        CrashMonitor.INSTANCE.init(this, object : IHandlerException {

            override fun handlerException(e: Throwable): Boolean {
                Toast.makeText(this@MyApp, "我关闭了一个异常页面", Toast.LENGTH_SHORT).show()
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