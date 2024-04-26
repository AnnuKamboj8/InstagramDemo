package com.example.instagarmdemo.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityLoginBinding
import com.example.instagarmdemo.extension.showToast
import com.example.instagarmdemo.ui.home.HomeActivity
import com.example.instagarmdemo.ui.mvvm.SignViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: SignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        init()
    }
    private fun init(){

        viewModel.loginResult.observe(this, Observer { result ->
            showToast(result)
            if (result == "Success") {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        binding.loginButton.setOnClickListener {
            if (binding.etEmailId.text.toString().equals("") or binding.etPassword.text.toString()
                    .equals("")
            ) {
                showToast(getString(R.string.fillDetail))
            } else {
                val email = binding.etEmailId.text.toString()
                val password = binding.etPassword.text.toString()
                viewModel.signIn(email, password)
            }
        }
        binding.createAccButton.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.createAccButton.setOnClickListener{
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}



/*   binding.loginButton.setOnClickListener{
           if(binding.etEmailId.text.toString().equals("") or binding.etPassword.text.toString().equals("")){
               showToast(getString(R.string.fillDetail))
           }  else{
               var  user = UserModel(binding.etEmailId.text.toString(),binding.etPassword.text.toString())
               FirebaseAuth.getInstance().signInWithEmailAndPassword(user.email.toString(),
                   user.password.toString())
                   .addOnCompleteListener {
                       if(it.isSuccessful){
                           val intent = Intent(this, HomeActivity::class.java)
                           startActivity(intent)
                           finish()
                       } else {

                           showToast(it.exception?.localizedMessage.toString())
                           Log.d("msg","${it.exception?.localizedMessage.toString()}")
                       }
                   }
           }
       }*/
/*    binding.loginButton.setOnClickListener {
        val email = binding.etEmailId.text.toString()
        val password = binding.etPassword.text.toString()

        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(this) {
                   showToast(getString(R.string.login_success))
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener(this) { e ->
                    when (e) {
                        is FirebaseAuthInvalidUserException -> {
                           showToast(getString(R.string.log_in_invalid_user))
                            Log.d("tag", "**** Invalid user: $e")

                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                           showToast(getString(R.string.log_in_invalid_credential)+email + getString(R.string.pass) + password)
                            Log.d("tag", "**** Invalid credentials: $e")
                        }
                        else -> {
                            Log.d("tag", "Other exception: $e")
                        }
                    }
                }
        } catch (e: Exception) {
            Log.d("tag", "Other exception: $e")
        }
    }*/
