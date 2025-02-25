package com.example.myapp

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.ColorPickerView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // 初始化当前设置
        initFontSize()
        initColorPickers()

        // 保存按钮
        findViewById<Button>(R.id.btnSave).setOnClickListener {
            saveSettings()
            Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    //字体大小
    private fun initFontSize() {
        val rgFontSize = findViewById<RadioGroup>(R.id.rgFontSize)
        when (Prefs.fontSize) {
            Prefs.FONT_SIZE_SMALL -> rgFontSize.check(R.id.rbSmall)
            Prefs.FONT_SIZE_NORMAL -> rgFontSize.check(R.id.rbNormal)
            Prefs.FONT_SIZE_LARGE -> rgFontSize.check(R.id.rbLarge)
        }

        rgFontSize.setOnCheckedChangeListener { _, checkedId ->
            Prefs.fontSize = when (checkedId) {
                R.id.rbSmall -> Prefs.FONT_SIZE_SMALL
                R.id.rbLarge -> Prefs.FONT_SIZE_LARGE
                else -> Prefs.FONT_SIZE_NORMAL
            }
        }
    }

    private fun initColorPickers() {
        // 背景颜色选择器
        val colorPickerBg = findViewById<ColorPickerView>(R.id.colorPickerBg)
        colorPickerBg.setColorListener { color, _ ->
            Prefs.bgColor = color
        }
        colorPickerBg.setColor(Prefs.bgColor)

        // 文字颜色选择器
        val colorPickerText = findViewById<ColorPickerView>(R.id.colorPickerText)
        colorPickerText.setColorListener { color, _ ->
            Prefs.textColor = color
        }
        colorPickerText.setColor(Prefs.textColor)
    }

    private fun saveSettings() {
        // 颜色选择器的值已在监听器中实时保存
        // 此处只需处理字体大小（已在RadioGroup监听器中保存）
    }
}