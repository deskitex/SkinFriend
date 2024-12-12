package com.example.skinfriend.ui.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.skinfriend.preferences.UserPreferences
import com.example.skinfriend.preferences.data.Question
import com.example.skinfriend.databinding.ActivityQuizBinding
import kotlinx.coroutines.launch

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding

    private var currentQuestionIndex = 0
    private val selectedScores = mutableListOf<Int>()

    private val questionList = listOf(
        Question(
            question = "Bagaimana Perasaanmu Hari Ini Secara Keseluruhan?",
            options = listOf("Tenang", "Agak cemas", "Cemas", "Sangat cemas"),
            scores = listOf(0, 1, 2, 3)
        ),
        Question(
            question = "Apakah Ada Hal yang Membuat Tertekan Hari Ini?",
            options = listOf("Tidak ada", "Sedikit", "Cukup banyak", "Sangat banyak"),
            scores = listOf(0, 1, 2, 3)
        ),
        Question(
            question = "Bagaimana Kualitas Tidurmu Semalam?",
            options = listOf("Tidur nyenyak", "Tidur cukup", "Sulit tidur", "Tidur terlalu lama"),
            scores = listOf(0, 1, 2, 3)
        )
    )

    private lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPreferences = UserPreferences.getInstance(this)

        loadQuestion()

        with(binding) {
            answer1.setOnClickListener { saveAnswerAndNext(0) }
            answer2.setOnClickListener { saveAnswerAndNext(1) }
            answer3.setOnClickListener { saveAnswerAndNext(2) }
            answer4.setOnClickListener { saveAnswerAndNext(3) }
        }
    }

    private fun loadQuestion() {
        if (currentQuestionIndex < questionList.size) {
            val question = questionList[currentQuestionIndex]
            with(binding) {
                questionBox.text = question.question
                answer1.text = question.options[0]
                answer2.text = question.options[1]
                answer3.text = question.options[2]
                answer4.text = question.options[3]
            }
        } else {
            showResult()
        }

        binding.okButton.setOnClickListener {
            finish()
        }
    }

    private fun saveAnswerAndNext(selectedOption: Int) {
        selectedScores.add(questionList[currentQuestionIndex].scores[selectedOption])
        currentQuestionIndex++
        loadQuestion()
    }

    private fun showResult() {
        val totalScore = selectedScores.sum()
        val (stressLevel, message) = when {
            totalScore <= 3 -> "Rendah" to "Tetap pertahankan kadar stresmu yang rendah agar tetap sehat dan produktif!"
            totalScore <= 6 -> "Biasa" to "Kendalikan stresmu dengan baik agar tidak memengaruhi aktivitas sehari-hari."
            else -> "Tinggi" to "Coba luangkan waktu untuk relaksasi atau cari bantuan jika stres terasa terlalu berat."
        }

        with(binding) {
            questionLayout.visibility = View.GONE
            resultLayout.visibility = View.VISIBLE
            resultText.text = stressLevel
            messageText.text = message
        }

        lifecycleScope.launch {
            userPreferences.saveStressQuizResult(stressLevel)
            Log.d("QuizActivity", "Last quiz Date: ${userPreferences.getLastQuizDate()}")
            Log.d("QuizActivity", "Current Date: ${userPreferences.getCurrentDateString()}")
            Log.d("QuizActivity", "Yesterday Date: ${userPreferences.getYesterdayDate()}")
        }
    }
}
