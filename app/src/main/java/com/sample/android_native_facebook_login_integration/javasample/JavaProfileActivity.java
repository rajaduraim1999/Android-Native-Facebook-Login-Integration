package com.sample.android_native_facebook_login_integration.javasample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sample.android_native_facebook_login_integration.R;

public class JavaProfileActivity extends AppCompatActivity {

    ImageView ivUser;
    TextView tvUserValue;
    String facebookData, facebookProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_profile);

        initUI();

        Intent intent = getIntent();

        facebookData = intent.getStringExtra("facebookData");
        facebookProfileImage = intent.getStringExtra("facebookProfileImage");

        Glide.with(JavaProfileActivity.this)
                .load(facebookProfileImage)
                .circleCrop()
                .into(ivUser);

        tvUserValue.setText(facebookData);
    }

    /**
     * The function initializes the user interface by finding and assigning values to the ImageView and
     * TextView objects.
     */
    private void initUI() {
        ivUser = findViewById(R.id.ivUser);
        tvUserValue = findViewById(R.id.tvUserValue);
    }
}