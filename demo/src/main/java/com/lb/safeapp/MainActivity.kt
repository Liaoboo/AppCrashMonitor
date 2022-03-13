package com.lb.safeapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.lb.keep.CrashKeepAlive
import com.lb.keep.CrashMonitor
import java.lang.NullPointerException

@CrashKeepAlive
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MyApp.topAct = this
        if (intent != null && intent.getStringExtra("next") != null) {
            val view = findViewById<View>(R.id.btn_next)
            view.visibility = View.GONE
            val view2 = findViewById<View>(R.id.btn_next2)
            view2.visibility = View.GONE
        }
    }

    fun click(v: View) {
        when (v.id) {
            R.id.btn_crash ->
                throw NullPointerException()
            R.id.btn_next ->
                startActivity(Intent(this, ExceptionActivity::class.java));
            else ->
                startActivity(Intent(this, MainActivity::class.java).putExtra("next", "xx"));
        }
    }
}