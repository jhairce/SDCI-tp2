package com.sdci.tp2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition{true}
        val auth = Firebase.auth

        if (auth.currentUser != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else if (auth.currentUser == null) {
            startActivity(Intent(this, IniciarSesion::class.java))
            finish()
        }

    }
}