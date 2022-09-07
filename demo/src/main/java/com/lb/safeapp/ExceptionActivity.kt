package com.lb.safeapp

import android.os.Bundle
import android.widget.Button

/**
 * Created by Liaobo
 */
class ExceptionActivity : BaseActivity() {
    init {
        throw NullPointerException("我是一个异常")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Button(this);
        btn.setText("点我，我是一个异常")
        btn.setOnClickListener { throw NullPointerException("我是一个异常") }
        setContentView(btn)
    }
}