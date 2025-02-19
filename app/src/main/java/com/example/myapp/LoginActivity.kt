package com.example.myapp

import android.content.Intent
import android.os.Bundle
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
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]

        binding.btnLogin.setOnClickListener { attemptLogin() }
        binding.btnRegister.setOnClickListener { attemptRegister() }
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
}