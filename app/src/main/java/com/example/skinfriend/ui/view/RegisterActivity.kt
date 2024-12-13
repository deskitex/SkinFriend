package com.example.skinfriend.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.skinfriend.R
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
    private val authService: ApiService by lazy { ApiConfig.getAuthService() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupUI()
    }

    private fun setupUI() {
        setupFieldValidation()
        setupGenderDropdown()

        binding.btnRegister.setOnClickListener { handleRegisterClick() }


        binding.tvLogin.setOnClickListener { navigateToLogin() }
    }

    private fun setupGenderDropdown() {
        val genderOptions = resources.getStringArray(R.array.gender_list)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)
    }

    private fun setupFieldValidation() {
        binding.etName.addTextChangedListener { validateName(it.toString()) }
        binding.etEmail.addTextChangedListener { validateEmail(it.toString()) }
        binding.etPhoneNumber.addTextChangedListener { validatePhoneNumber(it.toString()) }
        binding.etPassword.addTextChangedListener { validatePassword(it.toString()) }
        binding.etConfirmPassword.addTextChangedListener {
            validateConfirmPassword(binding.etPassword.text.toString(), it.toString())
        }
        binding.etGender.setOnItemClickListener { _, _, _, _ -> validateGender(binding.etGender.text.toString()) }
    }

    private fun handleRegisterClick() {
        val name = binding.etName.text.toString()
        val email = binding.etEmail.text.toString()
        val phoneNumber = binding.etPhoneNumber.text.toString()
        val gender = binding.etGender.text.toString()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        if (validateAllFields(name, email, phoneNumber, gender, password, confirmPassword)) {
            performRegister(name, email, phoneNumber, gender, password)
        }
    }

    private fun validateAllFields(
        name: String,
        email: String,
        phoneNumber: String,
        gender: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        val isNameValid = validateName(name)
        val isEmailValid = validateEmail(email)
        val isPhoneValid = validatePhoneNumber(phoneNumber)
        val isGenderValid = validateGender(gender)
        val isPasswordValid = validatePassword(password)
        val isConfirmPasswordValid = validateConfirmPassword(password, confirmPassword)

        return isNameValid && isEmailValid && isPhoneValid && isGenderValid && isPasswordValid && isConfirmPasswordValid
    }

    private fun validateName(name: String): Boolean {
        return if (name.isEmpty()) {
            binding.nameContainer.error = "Required*"
            false
        } else {
            binding.nameContainer.error = null
            true
        }
    }

    private fun validateEmail(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.emailContainer.error = "Required*"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailContainer.error = "Format email not valid!"
            false
        } else {
            binding.emailContainer.error = null
            true
        }
    }

    private fun validatePhoneNumber(phoneNumber: String): Boolean {
        return if (phoneNumber.isEmpty()) {
            binding.phoneNumberContainer.error = "Required*"
            false
        } else if (!phoneNumber.startsWith("08")) {
            binding.phoneNumberContainer.error = "Phone number must start with 08!"
            false
        } else if (!phoneNumber.matches(Regex("^\\d{10,13}$"))) {
            binding.phoneNumberContainer.error = "Phone number not valid!"
            false
        } else {
            binding.phoneNumberContainer.error = null
            true
        }
    }


    private fun validateGender(gender: String): Boolean {
        return if (gender.isEmpty()) {
            binding.genderContainer.error = "Please select gender"
            false
        } else {
            binding.genderContainer.error = null
            true
        }
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.isEmpty()) {
            binding.passwordContainer.error = "Required*"
            false
        } else if (password.length < 6) {
            binding.passwordContainer.error = "Password minimum 6 character!"
            false
        } else {
            binding.passwordContainer.error = null
            true
        }
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): Boolean {
        return if (confirmPassword.isEmpty()) {
            binding.confirmPasswordContainer.error = "Required*"
            false
        } else if (confirmPassword.length < 6) {
            binding.confirmPasswordContainer.error = "Password minimum 6 character!"
            false
        } else if (password != confirmPassword) {
            binding.confirmPasswordContainer.error = "Password not match!"
            false
        } else {
            binding.confirmPasswordContainer.error = null
            true
        }
    }

    private fun performRegister(
        name: String,
        email: String,
        phoneNumber: String,
        gender: String,
        password: String
    ) {
        Log.d("Data","Data $name $email $phoneNumber $gender $password")
        toggleLoading(true)
        val registerRequest = RegisterRequest(name = name, email = email, noTelp =  phoneNumber, gender = gender, password =  password)
        authService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>, response: Response<RegisterResponse>
            ) {
                toggleLoading(false)
                Log.d("Data", "$response")
                if (response.isSuccessful) {
                    handleRegisterSuccess(response.body())
                } else {
                    showToast("Registrasi gagal: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                toggleLoading(false)
                showToast("Terjadi kesalahan: ${t.message}")
            }
        })
    }

    private fun handleRegisterSuccess(response: RegisterResponse?) {
        if (response != null && response.error == false) {
            showToast("Registrasi berhasil, silakan login.")
            navigateToLogin()
        } else {
            val errorMessage = response?.message ?: "Registrasi gagal."
            showToast(errorMessage)
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.registerLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.tvRegister.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}
