package com.lb.keep

/**
 * activity申明该注解表示crash时不关闭该页面
 * Created by Liaobo
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CrashKeepAlive

