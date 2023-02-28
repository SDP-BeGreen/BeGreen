package com.github.sdp_begreen.begreen.coroutines
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//
// ------------------Coroutines with ViewModel example----------------------------
//https://developer.android.com/kotlin/coroutines#groovy
//
//class LoginViewModel(
//    private val loginRepository: LoginRepository
//): ViewModel() {
//
//    fun makeLoginRequest(username: String, token: String) {
//        viewModelScope.launch {
//            val jsonBody = "{ username: \"$username\", token: \"$token\"}"
//            val result = try {
//                loginRepository.makeLoginRequest(jsonBody)
//            } catch(e: Exception) {
//                Result.Error(Exception("Network request failed"))
//            }
//            when (result) {
//                is Result.Success<LoginResponse> -> // Happy path
//                else -> // Show error in UI
//            }
//        }
//    }
//}