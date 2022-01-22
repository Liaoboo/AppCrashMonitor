package com.lb.safeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lb.wrench.CrashKeepAlive
import java.lang.NullPointerException

@CrashKeepAlive
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun click(v: View) {
        if (v.id == R.id.btn_crash) {
            throw NullPointerException()
        } else {
            startActivity(Intent(this, ExceptionActivity::class.java))
        }
    }
}