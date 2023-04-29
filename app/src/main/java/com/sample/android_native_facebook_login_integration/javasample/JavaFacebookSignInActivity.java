package com.sample.android_native_facebook_login_integration.javasample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.sample.android_native_facebook_login_integration.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class JavaFacebookSignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FacebookSignIn";

    CallbackManager callbackManager;
    LoginManager loginManager;

    LoginButton lbFacebookSignIn;
    CardView cvGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java_facebook_sign_in);
        callbackManager = CallbackManager.Factory.create();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.sample.android_native_facebook_login_integration.javasample",
                    PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        initUI();

        loginManager = LoginManager.getInstance();
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                Log.e(TAG, "onSuccess: accessToken : " + accessToken);
                String token = accessToken.getToken();
                Log.e(TAG, "onSuccess: token : " + token);
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e(TAG, "onCompleted: " + object);

                        if (object != null) {
                            try {
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String fbUserId = object.getString("id");

                                Log.e(TAG, "onCompleted: name " + name);
                                Log.e(TAG, "onCompleted: email " + email);

                                String facebook_id = null, f_name = null, m_name = null, l_name = null, full_name = null, profile_image = null;
                                //By Profile Class
                                Profile profile = Profile.getCurrentProfile();
                                Log.e(TAG, "onSuccess: profile " + profile);
                                if (profile != null) {
                                    facebook_id = profile.getId();
                                    f_name = profile.getFirstName();
                                    m_name = profile.getMiddleName();
                                    l_name = profile.getLastName();
                                    full_name = profile.getName();
                                    profile_image = profile.getProfilePictureUri(400, 400).toString();

                                    Log.e(TAG, "onSuccess: facebook_id full_name : " + facebook_id + " f_name : " + f_name + " m_name : " + m_name + " l_name : " + l_name + " full_name : " + full_name + " profile_image : " + profile_image);

                                    String data = " name : " + name + "\n\n" + " email : " + email + "\n\n" + "facebook_id : " + facebook_id + "\n\n" + "f_name : " + f_name + "\n\n" + "m_name : " + m_name + "\n\n" + "l_name : " + l_name + "\n\n" + "full_name : " + full_name + "\n\n" + "token : " + token;
                                    Intent intent = new Intent(JavaFacebookSignInActivity.this, JavaProfileActivity.class);

                                    intent.putExtra("facebookData", data);
                                    intent.putExtra("facebookProfileImage", profile_image);
                                    startActivity(intent);

                                }
                                signOut();
                                // do action after Facebook login success
                                // or call your API
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                Log.e(TAG, "onSuccess: ");
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.e(TAG, "onCancel:  ");
                signOut();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                exception.printStackTrace();
                Log.e(TAG, "onError:  " + exception.getMessage());
            }
        });

        lbFacebookSignIn.setOnClickListener(this);
        cvGoogleSignIn.setOnClickListener(this);

    }

    private void initUI() {
        lbFacebookSignIn = findViewById(R.id.lbFacebookSignIn);
        cvGoogleSignIn = findViewById(R.id.cvFacebookSignIn);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lbFacebookSignIn:
            case R.id.cvFacebookSignIn:
                signIn();
                break;
        }
    }

    private void signIn() {
        loginManager.logInWithReadPermissions(JavaFacebookSignInActivity.this, Arrays.asList("email", "public_profile", "user_friends"));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void signOut() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/permissions/",
                null,
                HttpMethod.DELETE,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        LoginManager.getInstance().logOut();
                    }
                }).executeAsync();
    }

}