package com.example.instagarmdemo.ui.post

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.instagarmdemo.R
import com.example.instagarmdemo.databinding.ActivityReelsBinding
import com.example.instagarmdemo.extension.showToast
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
import com.iceteck.silicompressorr.SiliCompressor
import java.io.File
import java.net.URISyntaxException

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

                CompressVideo().execute("false", uri.toString(), Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString())
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


    @SuppressLint("StaticFieldLeak")
    private inner class CompressVideo : AsyncTask<String?, Uri?, String?>() {
        var dialog: Dialog? = null
        override fun doInBackground(vararg p0: String?): String? {
            var videoPath: String? = null
            try {

                val uri = Uri.parse(p0[1])

                videoPath = SiliCompressor.with(this@ReelsActivity)
                    .compressVideo(uri, p0[2])
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }

            return videoPath
        }

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            dialog = ProgressDialog.show(
                this@ReelsActivity, "", "Compressing..."
            )
        }


        @Deprecated("Deprecated in Java")
        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            dialog!!.dismiss()

            onCompressionComplete(s)
        }
    
    }

    private fun onCompressionComplete(compressedVideoPath: String?) {
        if (compressedVideoPath != null) {
            val compressedVideoFile = File(compressedVideoPath)
            val thumbnail = extractThumbnailFromVideo(compressedVideoFile)

            binding.postReel.setImageBitmap(thumbnail)

            uploadReels(Uri.fromFile(compressedVideoFile), Keys.REEL_FOLDER, progressDialog) { url ->
                if (url != null) {
                    videoUrl = url
                    showToast("Video uploaded successfully")
                } else {
                    showToast("Failed to upload video")
                }
            }
        } else {
            Toast.makeText(this, "Failed to compress video", Toast.LENGTH_SHORT).show()
        }
    }


    private fun init() {
        progressDialog =  ProgressDialog(this)
    }

    private fun extractThumbnailFromVideo(videoFile: File): Bitmap? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoFile.path)
        val frame = retriever.getFrameAtTime(0)
        retriever.release()
        return frame
    }
}





