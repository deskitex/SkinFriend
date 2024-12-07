package com.example.skinfriend.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.skinfriend.data.remote.response.RegisterRequest
import com.example.skinfriend.data.remote.response.RegisterResponse
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.remote.retrofit.ApiService
import com.example.skinfriend.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authService: ApiService by lazy {
        ApiConfig.getAuthService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupRegister()
    }

    private fun setupRegister() {
        binding.btnLogin.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            binding.nameContainer.error = null
            binding.emailContainer.error = null
            binding.passwordContainer.error = null

            if (name.isEmpty()) {
                binding.nameContainer.error = "Nama harus diisi!"
            }

            if (email.isEmpty()) {
                binding.emailContainer.error = "Email harus diisi!"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailContainer.error = "Format email tidak valid!"
            }

            if (password.isEmpty()) {
                binding.passwordContainer.error = "Password harus diisi!"
            } else if (password.length < 6) {
                binding.passwordContainer.error = "Password minimal 6 karakter!"
            }

            if (binding.nameContainer.error == null &&
                binding.emailContainer.error == null &&
                binding.passwordContainer.error == null
            ) {
                performRegister(name, email, password)
            }
        }

        binding.registerText.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun performRegister(name: String, email: String, password: String) {
        binding.progressBar.visibility = View.VISIBLE
        val registerRequest = RegisterRequest(name, email, password)
        authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                binding.progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && !body.error!!) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Registrasi berhasil, silakan login.",
                            Toast.LENGTH_SHORT
                        ).show()
                        navigateToLogin()
                    } else {
                        val errorMessage = body?.message ?: "Registrasi gagal."
                        Toast.makeText(
                            this@RegisterActivity,
                            errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi gagal: Email sudah terdaftar.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    this@RegisterActivity,
                    "Terjadi kesalahan: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
