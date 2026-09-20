package com.example.autoonceclick

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import kotlin.concurrent.thread

class AutoClickAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var isWaitRunning: Boolean = false
            private set

        private var waitThread: Thread? = null
        private var instance: AutoClickAccessibilityService? = null

        private const val MAX_WAIT_MS = 15 * 60 * 1000L // 15 分钟超时上限

        fun startWaitJob(targetTs: Long, x: Float, y: Float) {
            // 旧任务先停止
            stopCurrentJob()

            val svc = instance ?: return
            isWaitRunning = true

            waitThread = thread(name = "auto-click-wait") {
                try {
                    val now = System.currentTimeMillis()
                    var delay = targetTs - now
                    if (delay < 0) delay = 0
                    if (delay > MAX_WAIT_MS) delay = MAX_WAIT_MS

                    // 分段睡眠，便于中途取消
                    val step = 200L
                    var remaining = delay
                    while (remaining > 0 && isWaitRunning) {
                        val s = if (remaining > step) step else remaining
                        Thread.sleep(s)
                        remaining -= s
                    }

                    if (isWaitRunning) {
                        svc.performClick(x, y)
                    }
                } catch (_: InterruptedException) {
                    // 被取消
                } finally {
                    isWaitRunning = false
                }
            }
        }

        fun stopCurrentJob() {
            isWaitRunning = false
            waitThread?.interrupt()
            waitThread = null
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    override fun onUnbind(intent: android.content.Intent?): Boolean {
        instance = null
        stopCurrentJob()
        return super.onUnbind(intent)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // 不需要监听事件，仅用于执行手势
    }

    override fun onInterrupt() {}

    private fun performClick(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val stroke = GestureDescription.StrokeDescription(path, 0L, 50L)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }
}
