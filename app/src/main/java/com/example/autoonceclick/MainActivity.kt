package com.example.autoonceclick

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    lateinit var etHour: EditText
    lateinit var etMinute: EditText
    lateinit var etSecond: EditText
    lateinit var etMs: EditText
    lateinit var editX: EditText
    lateinit var editY: EditText
    lateinit var btnGoAccessibility: Button
    lateinit var btnStartTask: Button
    lateinit var btnStopTask: Button

    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sp = getSharedPreferences("click_setting", MODE_PRIVATE)

        etHour = findViewById(R.id.etHour)
        etMinute = findViewById(R.id.etMinute)
        etSecond = findViewById(R.id.etSecond)
        etMs = findViewById(R.id.etMs)
        editX = findViewById(R.id.editX)
        editY = findViewById(R.id.editY)
        btnGoAccessibility = findViewById(R.id.btnGoAccessibility)
        btnStartTask = findViewById(R.id.btnStartTask)
        btnStopTask = findViewById(R.id.btnStopTask)

        //读取本地保存；没有记录就使用初始默认值：6:59:59 ms=500
        val hSaved = sp.getInt("last_h",6)
        val mSaved = sp.getInt("last_m",59)
        val sSaved = sp.getInt("last_s",59)
        val msSaved = sp.getInt("last_ms",500)
        val xSaved = sp.getFloat("last_x",500f)
        val ySaved = sp.getFloat("last_y",800f)

        etHour.setText(hSaved.toString())
        etMinute.setText(mSaved.toString())
        etSecond.setText(sSaved.toString())
        etMs.setText(msSaved.toString())
        editX.setText(xSaved.toString())
        editY.setText(ySaved.toString())

        //打开无障碍设置页
        btnGoAccessibility.setOnClickListener {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
        }

        //启动任务：启动同时保存当前全部输入到本地
        btnStartTask.setOnClickListener {
            val h = etHour.text.toString().toInt()
            val m = etMinute.text.toString().toInt()
            val s = etSecond.text.toString().toInt()
            val msVal = etMs.text.toString().toInt()
            val xVal = editX.text.toString().toFloat()
            val yVal = editY.text.toString().toFloat()

            //持久化保存本次输入的值
            val editor = sp.edit()
            editor.putInt("last_h",h)
            editor.putInt("last_m",m)
            editor.putInt("last_s",s)
            editor.putInt("last_ms",msVal)
            editor.putFloat("last_x",xVal)
            editor.putFloat("last_y",yVal)
            editor.apply()

            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, h)
            cal.set(Calendar.MINUTE, m)
            cal.set(Calendar.SECOND, s)
            cal.set(Calendar.MILLISECOND, msVal)
            val targetTs = cal.timeInMillis

            AutoClickAccessibilityService.startWaitJob(targetTs, xVal, yVal)
        }

        btnStopTask.setOnClickListener {
            AutoClickAccessibilityService.stopCurrentJob()
        }

        //App打开，检测后台遗留任务弹窗
        if(AutoClickAccessibilityService.isWaitRunning){
            AlertDialog.Builder(this)
                .setTitle("发现遗留后台定时任务")
                .setMessage("后台还有尚未完成的点击计时任务。是否停止旧任务？")
                .setPositiveButton("停止旧任务"){ _,_ ->
                    AutoClickAccessibilityService.stopCurrentJob()
                }
                .setNegativeButton("保留继续运行",null)
                .show()
        }
    }
}
