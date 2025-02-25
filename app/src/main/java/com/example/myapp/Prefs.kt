package com.example.myapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color


object Prefs {
    private const val PREFS_NAME = "app_settings"
    private const val KEY_FONT_SIZE = "font_size"
    private const val KEY_BG_COLOR = "bg_color"
    private const val KEY_TEXT_COLOR = "text_color"

    private lateinit var prefs: SharedPreferences

    // 初始化方法（在 Application 或首个 Activity 中调用）
    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // 字体大小
    var fontSize: Float
        get() = prefs.getFloat(KEY_FONT_SIZE, FONT_SIZE_NORMAL)
        set(value) = prefs.edit().putFloat(KEY_FONT_SIZE, value).apply()

    // 背景颜色
    var bgColor: Int
        get() = prefs.getInt(KEY_BG_COLOR, Color.WHITE)
        set(value) = prefs.edit().putInt(KEY_BG_COLOR, value).apply()

    // 文字颜色
    var textColor: Int
        get() = prefs.getInt(KEY_TEXT_COLOR, Color.BLACK)
        set(value) = prefs.edit().putInt(KEY_TEXT_COLOR, value).apply()

    // 字体常量
    const val FONT_SIZE_SMALL = 20f
    const val FONT_SIZE_NORMAL = 40f
    const val FONT_SIZE_LARGE = 60f
}
