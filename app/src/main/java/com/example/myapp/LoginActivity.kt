package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Prefs.init(applicationContext)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnRegister.setOnClickListener { attemptRegister() }

        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        applySettings()

    }

    private fun attemptLogin() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名或密码不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = viewModel.login(username, password)
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("USER_ID", user.id)
                    }
                )
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "登录失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attemptRegister() {
        val username = binding.etUsername.text.toString()
        val password = binding.etPassword.text.toString()

        lifecycleScope.launch {
            if (viewModel.register(username, password)) {
                Toast.makeText(this@LoginActivity, "注册成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LoginActivity, "用户名已存在", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun applySettings() {
        // 应用背景色
        window.decorView.setBackgroundColor(Prefs.bgColor)

        // 应用字体大小和颜色
        val fontSize = Prefs.fontSize
        val textColor = Prefs.textColor

        listOf<TextView>(
            findViewById(R.id.etUsername),
            findViewById(R.id.etPassword),
            findViewById(R.id.btnLogin),
            findViewById(R.id.btnRegister)
        ).forEach {
            it.textSize = fontSize / resources.displayMetrics.scaledDensity
            it.setTextColor(textColor)
        }
    }

    override fun onResume() {
        super.onResume()
        applySettings() // 从设置界面返回时刷新
    }

}