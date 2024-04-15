package com.example.instagarmdemo.extension

import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class CommonExtension {
}

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun uploadImage(uri: Uri, folderName:String, callback:(String?)->Unit){
    var imageUri :String?= null
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUri=it.toString()
                callback(imageUri)
            }
        }

}

fun uploadReels(uri: Uri, folderName:String,progressDialog :ProgressDialog ,callback:(String?)->Unit){
    var imageUri :String?= null
    progressDialog.setTitle("Uploading . . .")
    progressDialog.show()
    FirebaseStorage.getInstance().getReference(folderName).child(UUID.randomUUID().toString())
        .putFile(uri)
        .addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                imageUri=it.toString()
                progressDialog.dismiss()
                callback(imageUri)
            }
        }

        .addOnProgressListener {
            val uploadVideo:Long =( it.bytesTransferred / it.totalByteCount)*100
            progressDialog.setMessage("Uploaded $uploadVideo%")
        }

}

fun SearchView.onTextChanged(block: (query: String, isEmpty: Boolean) -> Unit) {
    this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {

            if (query != null) {
                block(query, false)
                return true
            }
            return false
        }

        override fun onQueryTextChange(query: String?): Boolean {
            if (query != null && query.isEmpty()) {
                block(query, true)
                return true
            }

            if (query != null) {
                block(query, false)
                return true
            }
            return false
        }

    })


}