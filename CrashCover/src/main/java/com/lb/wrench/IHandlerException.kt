package com.lb.wrench

/**
 * Created by Liaobo
 */
interface IHandlerException {
    /**
     * 自定义异常处理
     * @return true：不执行内部默认关闭activity逻辑
     */
    fun handlerException(throwable: Throwable): Boolean = false

    /**
     * 没找到需要异常的activity或异常处理失败，退出app
     * @return true：不执行内部默认退出逻辑
     */
    fun notFindOrFail(throwable: Throwable): Boolean = false
}