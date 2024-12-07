package com.example.skinfriend.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.skinfriend.data.remote.response.LoginRequest
import com.example.skinfriend.data.remote.response.LoginResponse
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.remote.retrofit.ApiService
import com.example.skinfriend.databinding.ActivityLoginBinding
import com.example.skinfriend.helper.SessionManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    private val authService: ApiService by lazy {
        ApiConfig.getAuthService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SessionManager
        sessionManager = SessionManager(this)

        // Cek login status
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupLogin()
    }

    private fun setupLogin() {
        // Tombol login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            binding.emailContainer.error = null
            binding.passwordContainer.error = null

            if (email.isEmpty()) {
                binding.emailContainer.error = "Email harus terisi!"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailContainer.error = "Format email tidak valid!"
            }

            if (password.isEmpty()) {
                binding.passwordContainer.error = "Password harus terisi!"
            }

            if (binding.emailContainer.error == null && binding.passwordContainer.error == null) {
                performLogin(email, password)
            }
        }


        binding.btnLoginGoogle.setOnClickListener {
            Toast.makeText(this, "Fitur Login dengan Google belum tersedia", Toast.LENGTH_SHORT).show()
        }

        // Tombol register
        binding.registerText.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun performLogin(email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        val loginRequest = LoginRequest(email, password)
        authService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error!!) {
                        val loginResult = body.loginResult
                        if (loginResult?.token != null && loginResult.name != null) {
                            sessionManager.saveLoginSession(loginResult.token, loginResult.name)
                            Toast.makeText(
                                this@LoginActivity,
                                "Welcome, ${loginResult.name}",
                                Toast.LENGTH_SHORT
                            ).show()
                            navigateToMain()
                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "Login gagal: loginResult kosong",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        val errorMessage =
                            body?.message ?: "Login gagal: Tidak ada pesan kesalahan."
                        Toast.makeText(
                            this@LoginActivity,
                            "Login gagal: $errorMessage",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login gagal: Akun belum terdaftar",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@LoginActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
