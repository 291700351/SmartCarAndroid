package com.github.lee.smartcarandroid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import org.fusesource.hawtbuf.Buffer
import org.fusesource.hawtbuf.UTF8Buffer
import org.fusesource.mqtt.client.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    private lateinit var btn_main_stop: Button
    private lateinit var btn_main_forward: Button
    private lateinit var btn_main_back: Button
    private lateinit var btn_main_left: Button
    private lateinit var btn_main_right: Button
    private lateinit var connection: CallbackConnection

    private var ip = ""
    private var port: Int = -1

    companion object {
        private const val USER = "admin"
        private const val PASSWORD = "password"
        private const val TOPIC_OUT_MOVE = "car/move"
        fun starter(context: Context, ip: String, port: Int) {
            val bundle = Bundle()
            bundle.putString("ip", ip)
            bundle.putInt("port", port)
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (null == bundle) {
            finish()
            return
        }
        ip = bundle.getString("ip", "")
        if (TextUtils.isEmpty(ip)) {
            finish()
            return
        }
        port = bundle.getInt("port", -1)
        if (port == -1) {
            finish()
            return
        }

        Log.e("TAG", "$ip----------------------------------->$port")
        val mqtt = MQTT()
        mqtt.setHost(ip, port)
        mqtt.setUserName(USER)
        mqtt.setPassword(PASSWORD)

        connection = mqtt.callbackConnection()
        connection.listener(object : Listener {
            override fun onFailure(value: Throwable?) {
                Log.e("TAG", "Listener Error --->$value")
                runOnUiThread({
                    showToast("连接失败")
                })
            }

            override fun onPublish(topic: UTF8Buffer?, body: Buffer?, ack: Runnable?) {

            }

            override fun onConnected() {
                Log.e("TAG", "Connect")
            }

            override fun onDisconnected() {
                Log.e("TAG", "onDisconnected")
            }
        })
        object : Thread() {
            override fun run() {

                connection.connect(object : Callback<Void> {
                    override fun onSuccess(value: Void?) {
                        Log.e("TAG", "OK")
                        runOnUiThread { showToast("连接服务器成功") }
                    }

                    override fun onFailure(value: Throwable?) {
                        Log.e("TAG", "ERROR--->$value")
                        runOnUiThread { showToast("连接服务器失败:$value") }
                        value!!.printStackTrace()
                    }

                })
            }
        }.start()




        setContentView(R.layout.activity_main)

        btn_main_stop = findViewById(R.id.btn_main_stop)
        btn_main_forward = findViewById(R.id.btn_main_forward)
        btn_main_back = findViewById(R.id.btn_main_back)
        btn_main_left = findViewById(R.id.btn_main_left)
        btn_main_left = findViewById(R.id.btn_main_left)
        btn_main_right = findViewById(R.id.btn_main_right)

        btn_main_stop.setOnClickListener(this)
        btn_main_forward.setOnClickListener(this)
        btn_main_back.setOnClickListener(this)
        btn_main_left.setOnClickListener(this)
        btn_main_right.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        val message = when (view.id) {
            R.id.btn_main_stop -> "Stop"
            R.id.btn_main_forward -> "Forward"
            R.id.btn_main_back -> "Back"
            R.id.btn_main_left -> "Left"
            R.id.btn_main_right -> "Right"
            else -> "Stop"
        }
        object:Thread(){
            override fun run() {
                connection.publish(TOPIC_OUT_MOVE, message.toByteArray(), QoS.AT_LEAST_ONCE, false, object : Callback<Void> {
                    override fun onSuccess(value: Void?) {
                        showToast("发送成功")
                    }

                    override fun onFailure(value: Throwable?) {
                        showToast("发送失败：$value")
                    }
                })
            }
        }.start()

    }
}
