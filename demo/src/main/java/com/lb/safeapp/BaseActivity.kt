package com.lb.safeapp

import androidx.appcompat.app.AppCompatActivity

/**
 * Created by Liaobo
 */
open class BaseActivity : AppCompatActivity() {
    override fun onResume() {
        MyApp.topAct = this
        super.onResume()
    }
}