package com.example.instagarmdemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.instagarmdemo.ui.home.HomeActivity
import com.example.instagarmdemo.ui.login.SignUpActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.statusBarColor = Color.TRANSPARENT

        lifecycleScope.launch {
            delay(3000)
            if (FirebaseAuth.getInstance().currentUser == null)
                startActivity(Intent(this@MainActivity, SignUpActivity::class.java))
            else
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        }

    }
}