package com.orienteering.handrail.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.orienteering.handrail.R
import com.orienteering.handrail.home_menu.HomeActivity
import com.orienteering.handrail.welcome.WelcomeActivity

class SplashView : AppCompatActivity(), ISplashContract.ISplashView {
    val handler : Handler = Handler();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val presenter : ISplashContract.ISplashPresenter = SplashPresenter(this)
        presenter.getData()
    }

    /**
     * Initiate homeactivity on successful login
     *
     */
    override fun onResponseSuccess() {
        handler.postDelayed(Runnable() { run() {         val intent = Intent(this@SplashView, HomeActivity::class.java).apply {}
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent) } },500);
    }

    override fun onResponseFailure() {
        handler.postDelayed(Runnable() {    },500);
    }

    override fun onresponseError() {
        handler.postDelayed(Runnable() { run() {        val intent = Intent(this@SplashView, WelcomeActivity::class.java).apply {}
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this@SplashView, "Error: Connection unavailable, please try again later",Toast.LENGTH_LONG).show()} },500);
    }
}
