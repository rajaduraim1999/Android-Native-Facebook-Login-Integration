package com.sample.android_native_facebook_login_integration.kotlinsample

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sample.android_native_facebook_login_integration.R

class KotlinProfileActivity : AppCompatActivity() {
    var ivUser: ImageView? = null
    var tvUserValue: TextView? = null
    var googleData: String? = null
    var googleProfileImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_profile)
        initUI()
        val intent = intent
        googleData = intent.getStringExtra("googleData")
        googleProfileImage = intent.getStringExtra("googleProfileImage")
        Glide.with(this@KotlinProfileActivity)
            .load(googleProfileImage)
            .circleCrop()
            .into(ivUser!!)
        tvUserValue!!.text = googleData
    }

    /**
     * The function initializes UI elements in a Kotlin program.
     */
    private fun initUI() {
        ivUser = findViewById(R.id.ivUser)
        tvUserValue = findViewById(R.id.tvUserValue)
    }
}