package com.kontakanprojects.apptkslb.view.splash

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kontakanprojects.apptkslb.R
import com.kontakanprojects.apptkslb.session.UserPreference
import com.kontakanprojects.apptkslb.view.auth.AuthActivity
import com.kontakanprojects.apptkslb.view.auth.ChooseLoginFragment
import com.kontakanprojects.apptkslb.view.guru.GuruActivity
import com.kontakanprojects.apptkslb.view.siswa.SiswaActivity
import kotlinx.coroutines.*

class SplashScreenActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val DELAY = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_splash_screen)

        activityScope.launch {
            delay(DELAY)

            // check session login
            val session = UserPreference(this@SplashScreenActivity)
            if (session.getLogin().isLoginValid) {
                when (session.getUser().idRole) {
                    ChooseLoginFragment.ROLE_GURU -> {
                        val intent = Intent(this@SplashScreenActivity, GuruActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    ChooseLoginFragment.ROLE_SISWA -> {
                        val intent = Intent(this@SplashScreenActivity, SiswaActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val intent = Intent(this@SplashScreenActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}