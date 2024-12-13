package com.example.skinfriend.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.example.skinfriend.BuildConfig
import com.example.skinfriend.R
import com.example.skinfriend.data.remote.response.LoginRequest
import com.example.skinfriend.data.remote.response.LoginResponse
import com.example.skinfriend.data.remote.retrofit.ApiConfig
import com.example.skinfriend.data.remote.retrofit.ApiService
import com.example.skinfriend.databinding.ActivityLoginBinding
import com.example.skinfriend.helper.SessionManager
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    private val authService: ApiService by lazy { ApiConfig.getAuthService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) navigateToMain()

        setupUI()
        observeFields()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener { handleLoginClick() }
        binding.btnLoginGoogle.setOnClickListener { performLoginGoogle() }
        binding.registerText.setOnClickListener { navigateToRegister() }
    }

    private fun observeFields() {
        binding.etEmail.addTextChangedListener { text ->
            binding.emailContainer.error = if (text.isNullOrEmpty()) {
                "Email harus terisi!"
            } else if (!Patterns.EMAIL_ADDRESS.matcher(text).matches()) {
                "Format email tidak valid!"
            } else null
        }

        binding.etPassword.addTextChangedListener { text ->
            binding.passwordContainer.error = if (text.isNullOrEmpty()) {
                "Password harus terisi!"
            } else null
        }
    }

    private fun handleLoginClick() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (validateFields()) {
            performLogin(email, password)
        }
    }

    private fun validateFields(): Boolean {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val isEmailValid = email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty()

        return isEmailValid && isPasswordValid
    }

    private fun performLogin(email: String, password: String) {
        toggleLoading(isLoading = true, isGoogleLogin = false)

        val loginRequest = LoginRequest(email, password)
        authService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                toggleLoading(isLoading = false, isGoogleLogin = false)
                if (response.isSuccessful) {
                    handleLoginSuccess(response.body(), email)
                } else {
                    showToast("Login gagal: Akun belum terdaftar")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                toggleLoading(isLoading = false, isGoogleLogin = false)
                showToast("Terjadi kesalahan: ${t.message}")
            }
        })
    }

    private fun handleLoginSuccess(response: LoginResponse?, email: String) {
        response?.let {
            if (!it.error!!) {
                val loginResult = it.loginResult
                if (loginResult?.token != null && loginResult.name != null) {
                    sessionManager.saveLoginSession(loginResult.token, loginResult.name, email)
                    showToast("Welcome, ${loginResult.name}")
                    navigateToMain()
                } else {
                    showToast("Login gagal: loginResult kosong")
                }
            } else {
                showToast(it.message ?: "Login gagal: Tidak ada pesan kesalahan.")
            }
        } ?: showToast("Login gagal: Response kosong.")
    }

    private fun performLoginGoogle() {
        toggleLoading(isLoading = true, isGoogleLogin = true)
        val credentialManager = androidx.credentials.CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = androidx.credentials.GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val response = credentialManager.getCredential(this@LoginActivity, request)
                handleSignInGoogle(response)
            } catch (e: androidx.credentials.exceptions.GetCredentialException) {
                toggleLoading(isLoading = false, isGoogleLogin = true)
                val message = e.message ?: "Terjadi kesalahan"
                showToast(if (message.contains("activity is cancelled", true)) {
                    "Autentikasi dibatalkan oleh pengguna."
                } else {
                    "Terjadi kesalahan: $message"
                })
            }
        }
    }

    private fun handleSignInGoogle(response: GetCredentialResponse) {
        val credential = response.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener { task ->
            toggleLoading(isLoading = false, isGoogleLogin = true)
            if (task.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    sessionManager.saveLoginSession(idToken, user.displayName.orEmpty(), user.email.orEmpty())
                    showToast("Welcome, ${user.displayName}")
                    navigateToMain()
                } else {
                    showToast("Tidak Berhasil Login!")
                }
            } else {
                showToast("Gagal login dengan Google.")
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean, isGoogleLogin: Boolean) {
        if (isGoogleLogin) {
            binding.btnLoginGoogle.isEnabled = !isLoading
            binding.loginGoogleLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvLoginGoogle.visibility = if (isLoading) View.GONE else View.VISIBLE
        } else {
            binding.btnLogin.isEnabled = !isLoading
            binding.loginLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.tvLogin.visibility = if (isLoading) View.GONE else View.VISIBLE
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
}
