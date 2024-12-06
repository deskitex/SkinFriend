package com.example.skinfriend.ui.view.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.skinfriend.databinding.ActivityLoginBinding
import com.example.skinfriend.helper.SessionManager
import com.example.skinfriend.ui.view.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek login status
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        // Inisialisasi tampilan login
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup tombol login
        binding.btnLogin.setOnClickListener {
            val username = binding.username.text.toString()
            val password = binding.password.text.toString()

            if (validateLogin(username, password)) {
                // Simpan sesi login
                val token = "dummy_token" // Ganti dengan token asli dari server Anda
                sessionManager.saveLoginSession(token, username)
                Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()

                // Arahkan ke MainActivity
                navigateToMain()
            } else {
                Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateLogin(username: String, password: String): Boolean {
        // Contoh validasi sederhana (gantikan dengan validasi sebenarnya)
        return username.isNotEmpty() && password == "password123"
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
