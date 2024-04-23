package com.example.instagarmdemo.ui.post

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityPostBinding
import com.example.instagarmdemo.extension.showToast
import com.example.instagarmdemo.extension.uploadImage
import com.example.instagarmdemo.ui.Models.Post
import com.example.instagarmdemo.ui.Models.Story
import com.example.instagarmdemo.ui.home.HomeActivity
import com.example.instagarmdemo.ui.Models.UserModel
import com.example.instagarmdemo.utility.Keys
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase
import java.util.UUID
import kotlin.random.Random


class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private var imageUrl: String? = null
    private var timeString :String=" "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post)
        var fromStory = intent.getBooleanExtra(Keys.FROM_STORY,false)
        init()
        if(fromStory)
        {
            postStory()
        }
        else{
       postImage()
        }
    }

    private fun postStory() {
        binding.materialToolBar.title= getString(R.string.add_story)
        binding.caption.visibility=View.GONE
        val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                uploadImage(uri, Keys.STORY_FOLDER) { url ->
                    if (url != null) {
                        binding.postImage.setImageURI(uri)
                        imageUrl = url



                    }
                }
            }
        }
        binding.postImage.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.postButton.setOnClickListener {
            Firebase.firestore.collection(Keys.USER_NODE)
                .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                .addOnSuccessListener {
                    val user = it.toObject<UserModel>()!!

                    if (imageUrl != null) {
                        val story: Story = Story(
                            storyUrl = imageUrl!!,
                            uid = FirebaseAuth.getInstance().currentUser!!.uid,
                            time = System.currentTimeMillis().toString(),
                            storyId = UUID.randomUUID().toString()
                        )


                        Firebase.firestore.collection(Keys.STORY).document().set(story)
                            .addOnSuccessListener {
                               updateHasAddedStory(imageUrl.toString(),System.currentTimeMillis().toString())
                                startActivity(Intent(this, HomeActivity::class.java))
                                finish()
                                /* Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid)
                            .document().set(post).addOnSuccessListener {

                            }*/
                            }
                    } else {
                        showToast(getString(R.string.upload_validation_msg))
                    }
                }
        }

        binding.cancelButton.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }



    }

    private fun updateHasAddedStory(imageUrl:String,storyTime:String) {
        val currentUserUid = FirebaseAuth.getInstance().currentUser!!.uid
        val userDocRef = Firebase.firestore.collection(Keys.USER_NODE).document(currentUserUid)

        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserModel>()
                user?.let { userModel ->
                    val updates = mapOf(
                        "hasAddedStory" to true,
                        "storyImageUrl" to imageUrl,
                        "storyTime" to storyTime
                    )
                    userDocRef.update(updates)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { exception ->
                            // Handle failure to update fields
                        }
                }
            }

    }

    private fun init() {

        setSupportActionBar(binding.materialToolBar)
        val upArrow = ContextCompat.getDrawable(this,
            R.drawable.ic_baseline_arrow_back_24)
        upArrow?.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        binding.materialToolBar.setNavigationOnClickListener {
            finish()
        }
    }


        private fun postImage() {
            val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                uri?.let {
                    uploadImage(uri, Keys.POST_FOLDER) { url ->
                        if (url != null) {
                            binding.postImage.setImageURI(uri)
                            imageUrl = url


                        }
                    }
                }
            }
            binding.postImage.setOnClickListener {
                launcher.launch("image/*")
            }
            binding.postButton.setOnClickListener {
                Firebase.firestore.collection(Keys.USER_NODE)
                    .document(FirebaseAuth.getInstance().currentUser!!.uid).get()
                    .addOnSuccessListener {
                        val user = it.toObject<UserModel>()!!

                        if (imageUrl != null) {
                            val post: Post = Post(
                                postUrl = imageUrl!!,
                                caption = binding.caption.editableText.toString(),
                                uid = FirebaseAuth.getInstance().currentUser!!.uid,
                                time = System.currentTimeMillis().toString()
                            )

                            Firebase.firestore.collection(Keys.POST).document().set(post)
                                .addOnSuccessListener {
                                    startActivity(Intent(this, HomeActivity::class.java))
                                    finish()
                                    /* Firebase.firestore.collection(FirebaseAuth.getInstance().currentUser!!.uid)
                                .document().set(post).addOnSuccessListener {

                                }*/
                                }
                        } else {
                            showToast(getString(R.string.upload_validation_msg))
                        }
                    }
            }

            binding.cancelButton.setOnClickListener {
                imageUrl = null
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }



        }







}