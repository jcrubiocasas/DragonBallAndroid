package com.jcrubio.dragonball.Login.ViewController

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.jcrubio.dragonball.Login.ViewModel.LoginViewModel
import com.jcrubio.dragonball.Home.ViewController.HomeActivity
import com.jcrubio.dragonball.R
import com.jcrubio.dragonball.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val TAG_EMAIL = "Email"
    private val TAG_PASSWROD = "Password"

    private lateinit var binding: ActivityMainBinding

    private val viewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUserInterface()

        lifecycleScope.launch {
            viewModel.uiState.collect {
                when (it) {
                    is LoginViewModel.UiState.Started -> Log.w("TAG", "Iniciado")
                    is LoginViewModel.UiState.Ended -> Log.w("TAG", "Terminado")
                    //is LoginViewModel.UiState.OnLoginCompleted -> Log.w("TAG", "LOGIN Completo")
                    // Cuando funcione bien el login
                    is LoginViewModel.UiState.OnLoginCompleted -> HomeActivity.launch(
                        binding.swSave.context,
                        viewModel.token
                    )

                    is LoginViewModel.UiState.Error -> Log.w("TAG", "Error en UiState")
                }
            }
        }
    }

    //SetUserInterface
    private fun setUserInterface() {
        loadDataFromPreferences()
        setActionMethod()
    }

    //Button on Click
    private fun setActionMethod() {

        binding.swSave.setOnClickListener {
            if (!binding.swSave.isChecked) {
                saveDataInPreferences("", "")
                Toast.makeText(this, getString(R.string.login_data_removed), Toast.LENGTH_LONG)
                    .show()
                loadDataFromPreferences()
            }
        }

        binding.bnLogin.setOnClickListener {

            if (!binding.etEmail.text.isEmpty() && !binding.etPass.text.isEmpty()) {
                if (binding.swSave.isChecked) {
                    saveDataInPreferences(
                        binding.etEmail.text.toString(),
                        binding.etPass.text.toString()
                    )
                    Toast.makeText(this, getString(R.string.login_data_saved), Toast.LENGTH_LONG)
                        .show()
                } else {
                    Log.w("Tag", "Login data will not be saved")
                }
                viewModel.tryLogin(binding.etEmail.text.toString(), binding.etPass.text.toString())
                Log.w("Login", "Inside the login call method")
            }
        }
    }

    //SharedPreferences related Methods
//Save in saveInPreferences
    private fun saveDataInPreferences(mail: String, pass: String) {
        getPreferences(Context.MODE_PRIVATE).edit().apply {
            putString(TAG_EMAIL, mail).apply()
            putString(TAG_PASSWROD, pass).apply()

        }
    }

    //Retrieve
    private fun loadDataFromPreferences() {
        getPreferences(Context.MODE_PRIVATE).apply {
            binding.etEmail.setText(getString(TAG_EMAIL, ""))
            binding.etPass.setText(getString(TAG_PASSWROD, ""))
            if (binding.etEmail.text.toString() != "" && binding.etPass.text.toString() != "") {
                binding.swSave.isChecked = true
            }
        }
    }

}