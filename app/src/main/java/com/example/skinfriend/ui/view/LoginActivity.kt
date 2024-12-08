package com.example.skinfriend.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.skinfriend.BuildConfig
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

    private val authService: ApiService by lazy {
        ApiConfig.getAuthService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        if (sessionManager.isLoggedIn()) {
            navigateToMain()
//            return
        }

        setupLogin()
    }

    private fun setupLogin() {
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
            performLoginGoogle()
        }

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

    private fun performLoginGoogle() {

        Toast.makeText(this, "PerformLoginGoogle...", Toast.LENGTH_SHORT).show()

        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.WEB_CLIENT_ID)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val response: GetCredentialResponse = credentialManager.getCredential(
                    context = this@LoginActivity,
                    request = request
                )
                handleSignInGoogle(response)
            } catch (e: GetCredentialException) {
                binding.progressBar.visibility = View.GONE
                if (e.message != null && e.message!!.contains(
                        "activity is cancelled by the user",
                        ignoreCase = true
                    )
                ) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Autentikasi dibatalkan oleh pengguna.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Terjadi kesalahan: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun handleSignInGoogle(res: GetCredentialResponse) {
        Toast.makeText(this, "handleSigninGoogle...", Toast.LENGTH_SHORT).show()
        when (val credential = res.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        Toast.makeText(this, "firebaseAuthWithGoogle...", Toast.LENGTH_SHORT).show()
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBar.visibility = View.GONE
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        Toast.makeText(this, "Berhasil Login !", Toast.LENGTH_SHORT).show()
                        sessionManager.saveLoginSession(idToken, user.displayName.toString())
                        navigateToMain()
                    } else {
                        Toast.makeText(this, "Tidak Berhasil Login !", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
    }

    private fun navigateToMain() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}
