package com.lb.safeapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.lang.NullPointerException

/**
 * Created by Liaobo
 */
class ExceptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val btn = Button(this);
        btn.setText("点我，我是一个异常")
        btn.setOnClickListener { throw NullPointerException("我是一个异常") }
        setContentView(btn)
    }
}