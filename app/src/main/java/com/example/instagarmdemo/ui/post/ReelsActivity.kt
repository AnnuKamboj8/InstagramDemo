package com.example.instagarmdemo.ui.post

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityReelsBinding
import com.example.instagarmdemo.extension.uploadImage
import com.example.instagarmdemo.extension.uploadReels
import com.example.instagarmdemo.ui.Models.Reel
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.ui.home.HomeActivity
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import android.os.Bundle as Bundle1

class ReelsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReelsBinding
    private lateinit var progressDialog: ProgressDialog
    private var videoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle1?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reels)
        init()
        postReel()
    }

    private fun postReel() {
        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadReels(uri, Keys.REEL_FOLDER,progressDialog) { url ->
                    if (url != null) {

                        videoUrl = url

                    }
                }
            }
        }
        binding.postReel.setOnClickListener {
            launcher.launch("video/*")
        }
        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(Keys.USER_NODE).document(Firebase.auth.currentUser!!.uid).get().addOnSuccessListener {
                var user = it.toObject<UserModel>()
                val reel: Reel = Reel(videoUrl!!,
                    binding.caption.editableText.toString(),
                    user!!.image!!,
                    user.name,
                    user.uid
                )
                Firebase.firestore.collection(Keys.REEL).document().set(reel).addOnSuccessListener {
                    Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid + Keys.REEL)
                        .document().set(reel).addOnSuccessListener {
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                }
            }

        }
        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }


    private fun init() {
        progressDialog =  ProgressDialog(this)

    }
}