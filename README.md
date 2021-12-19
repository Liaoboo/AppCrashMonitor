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
## 拦截后处理
追溯Exception的堆栈，遍历className，销毁匹配到靠近栈顶端的Activiyt，如果没找到则关闭top的Activity（因为多数情况下是top的Activity任务造成的）；
``` 
private fun finishExceptionActivity(e: Throwable): Boolean {
    ...
    
    for (element in elements) {
        val cls = Class.forName(elements[0].className)
        if (Activity::class.java.isAssignableFrom(cls)) {
            val activities: List<Activity> = Utils.getActivityList()
            for (activity in activities) {
                if (activity.javaClass == cls) {
                    activity.finish()
                    activity.overridePendingTransition(0, 0)
                    isFindAndFinish = true
                }
            }
            break
        }
    }

    ...

    return isFindAndFinish
}
```
考虑场景：如果没有找到Activity场景，或者出错，那就执行重启或者杀进程策略，避免因为错误影响其他业务流程照成错误。
