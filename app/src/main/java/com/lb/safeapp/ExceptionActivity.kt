package com.lb.safeapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.NullPointerException

/**
 * Created by Liaobo
 */
class ExceptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        throw NullPointerException("我是一个异常")
    }
}