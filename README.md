# AppCrashMonitor
Android APP Crash 防护，核心方法也封装成lib库：CrashCover

## 背景
很多场景下由于一些微不足道的bug导致app崩溃很可惜，Android 系统默认的异常后直接杀进程机制简单粗暴，但很多时候让app崩溃其实也并不能解决问题，同时造成的用户体验非常不好。

## 思考
1、App 的崩溃怎么拦截？

2、如果我们拦截了一个主线程的崩溃，让app不崩溃，用户还能正常使用吗？

3、如果不能正常使用是为什么？

4、为何UI体系下单线程模型？

5、有什么更好的处理办法呢？三方库、底层以及硬件导致的崩溃怎么拦截？

## 核心思想
``` 
Handler(Looper.getMainLooper()).post {
    while (true) {
        try {
            Looper.loop()
        } catch (e: Exception) {
            e.printStackTrace()
            //do something
        }
    }
}
```
## 核心原理简述
主要是利用Android的Looper机制（进行偷梁换柱）。
![](https://github.com/Liaoboo/AppCrashMonitor/blob/main/img_folder/img1.jpeg)

## 拦截后处理
追溯Exception的堆栈，遍历className，销毁匹配到靠近栈顶端的Activity，如果没找到则关闭top的Activity（因为多数情况下是top的Activity任务造成的）；

``` 
private fun finishStackTraceExceptionActivity(e: Throwable): Boolean {
    ...
    
    for (element in elements) {
        val cls = Class.forName(elements[0].className)
        if (Activity::class.java.isAssignableFrom(cls)) {
            val activities: List<Activity> = lifecycleImpl.getActivityList()
            for (activity in activities) {
                if (activity.javaClass == cls) {
                    activity.finish()
                    activity.overridePendingTransition(0, 0)
                    return true
                }
            }
            
        }
    }

    ...

    return false
}
```
注意：从Activity创建到onCreate()这段时间的生命周期中如果出现异常，这个时候去保护就会存在黑屏的问题，应该单独处理这种场景。
```
private fun finishInitExceptionActivity(throwable: Throwable): Boolean { ... }
```
##目前库的处理流程如下：
![考虑场景：如果没有找到Activity场景，或者出错，那就执行重启或者杀进程策略，避免因为错误影响其他业务流程照成错误。](https://github.com/Liaoboo/AppCrashMonitor/blob/main/img_folder/img2.jpeg)


