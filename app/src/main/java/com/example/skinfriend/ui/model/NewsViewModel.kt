package com.example.skinfriend.ui.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skinfriend.data.remote.response.ArticlesItem
import com.example.skinfriend.data.repository.NewsRepository
import kotlinx.coroutines.launch

class NewsViewModel(private val newsRepository: NewsRepository) : ViewModel() {
    val listNews: LiveData<List<ArticlesItem>> = newsRepository.listNews

    private val _isLoading = newsRepository.isLoading
    val isLoading: LiveData<Boolean> = _isLoading

    fun showListNews() {
        viewModelScope.launch{
            newsRepository.showListNews()
        }
    }
}