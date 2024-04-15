package com.example.instagarmdemo.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivitySignUpBinding
import com.example.instagarmdemo.extension.showToast
import com.example.instagarmdemo.extension.uploadImage
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.home.HomeActivity
import com.example.instagarmdemo.ui.mvvm.HomeViewModel
import com.example.instagarmdemo.ui.mvvm.SignViewModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var user: UserModel
    private lateinit var context: Context
    private val viewModel: SignViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        init()
    }

    private fun init() {
        context = this
        user = UserModel()
        if (intent.hasExtra("MODE")) {
            if (intent.getIntExtra("MODE", -1) == 1) {
                binding.registerButton.text = getString(R.string.update_profile)
                Firebase.firestore.collection(Keys.USER_NODE)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                    .addOnSuccessListener {
                        user = it.toObject<UserModel>()!!

                        if (!user.image.isNullOrEmpty()) {
                            Glide.with(context).load(user.image).into(binding.profileImage)
                        }
                        binding.etName.setText(user.name)
                        binding.etEmailId.setText(user.email)
                        binding.etPassword.setText(user.password)
                    }
            }
        }
        binding.loginTextView.setOnClickListener {
            val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadImage(uri, Keys.USER_PROFILE) {
                    if (it != null) {
                        user.image = it
                        binding.profileImage.setImageURI(uri)
                    }
                }
            }
        }
        binding.profileImage.setOnClickListener {
            launcher.launch("image/*")
        }

        binding.registerButton.setOnClickListener {
            if (intent.hasExtra("MODE")) {
                if (intent.getIntExtra("MODE", -1) == 1) {
                    viewModel.updateUserProfile(user,
                        onSuccess = {
                            showToast(getString(R.string.success_update_msg))
                            startActivity(Intent(this, HomeActivity::class.java))
                        },
                        onFailure = { errorMessage ->
                            showToast(errorMessage)
                        }
                    )
                }
                   /* Firebase.firestore.collection(Keys.USER_NODE)
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .set(user)
                        .addOnSuccessListener {
                            showToast(" Successfully Updated")
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                }*/
            } else {
                val email = binding.etEmailId.text.toString()
                val password = binding.etPassword.text.toString()
                val name = binding.etName.text.toString()
                signUpWithEmailAndPassword(email, name, password)
            }
        }
    }

    private fun signUpWithEmailAndPassword(email: String, name: String, password: String) {
        if (binding.etName.text.toString().isEmpty() || binding.etEmailId.text.toString().isEmpty()
            || binding.etPassword.text.toString().isEmpty()
        ) {
            showToast(getString(R.string.fill_info_msg))
        } else {
            val email = binding.etEmailId.text.toString()
            val password = binding.etPassword.text.toString()
            val name = binding.etName.text.toString()

            viewModel.createUserWithEmailAndPassword(email, name, password,
                onSuccess = {
                    showToast(getString(R.string.authentication_msg))
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onFailure = { errorMessage ->
                    showToast(errorMessage)
                }
            )
        }
        }
    }





/* if (intent.hasExtra("MODE")) {
    *//*   if (intent.getIntExtra("MODE", -1) == 1) {
                    Firebase.firestore.collection(Keys.USER_NODE)
                        .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                        .set(user)
                        .addOnSuccessListener {
                            showToast(" Successfully Updated")
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                }*//*
            } else {
                if (binding.etName.text.toString().equals("") or binding.etEmailId.text.toString()
                        .equals("") or binding.etPassword.text.toString().equals("")
                ) {

                    showToast("Please fill all information")
                } else {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                        binding.etEmailId.text.toString(), binding.etPassword.toString()
                    ).addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            user.name = binding.etName.text.toString()
                            user.email = binding.etEmailId.text.toString()
                            user.password = binding.etPassword.text.toString()
                            Firebase.firestore.collection(Keys.USER_NODE)
                                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())
                                .set(user)
                                .addOnSuccessListener {
                                    showToast("Login Successful")
                                    startActivity(Intent(this, LoginActivity::class.java))
                                }


                        } else {
                            showToast(result.exception?.localizedMessage.toString())
                        }
                    }
                }
            }*/
