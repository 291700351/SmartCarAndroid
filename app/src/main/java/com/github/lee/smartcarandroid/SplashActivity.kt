package com.github.lee.smartcarandroid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast

class SplashActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var et_splash_ip: EditText
    private lateinit var et_splash_port: EditText
    private lateinit var cb_splash_save: CheckBox


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)
        et_splash_ip = findViewById(R.id.et_splash_ip)
        et_splash_port = findViewById(R.id.et_splash_port)
        cb_splash_save = findViewById(R.id.cb_splash_save)

        findViewById<View>(R.id.btn_splash_connect).setOnClickListener(this)

        val sp = getSharedPreferences("config", Context.MODE_PRIVATE)
        val ip = sp.getString("ip", "")
        val port = sp.getString("port", "")
        et_splash_ip.setText(ip)
        et_splash_port.setText(port)
        cb_splash_save.isChecked = true


    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_splash_connect -> {
                connectMQTT()
            }
        }
    }

    private fun connectMQTT() {
        val ip = et_splash_ip.text.toString().trim()
        if (TextUtils.isEmpty(ip)) {
            showToast("请输入服务器IP")
            return
        }
        val port = et_splash_port.text.toString().trim().toInt()


        val sp = getSharedPreferences("config", Context.MODE_PRIVATE)
        val editor = sp.edit()
        if (cb_splash_save.isChecked) {
            //保存
            editor.putString("ip", ip)
            editor.putString("port", port.toString())
        } else {
            editor.remove("ip")
            editor.remove("port")
        }
        editor.apply()
        //跳转主页面  携带ip和port
        MainActivity.starter(this, ip, port)
        finish()

    }


}

fun AppCompatActivity.showToast(text: String) {
    runOnUiThread({
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    })
}