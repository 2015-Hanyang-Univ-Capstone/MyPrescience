package com.myprescience.application.myprescience;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;


public class LoginActivity extends FragmentActivity {
    private MainFragment mainFragment;
    private Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (savedInstanceState == null) {
//            // Add the fragment on initial activity setup
//            mainFragment = new MainFragment();
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(android.R.id.content, mainFragment)
//                    .commit();
//        } else {
//            // Or set the fragment from restored state info
//            mainFragment = (MainFragment) getSupportFragmentManager()
//                    .findFragmentById(android.R.id.content);
//        }

        setContentView(R.layout.activity_login);

        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("public_profile"));
        authButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if(graphUser != null){
                    Toast.makeText(LoginActivity.this, graphUser.getName()+"로 로그인되었습니다!", Toast.LENGTH_SHORT);
                }
            }
        });

        // 임시용 게스트 로그인 버튼
        guestButton = (Button) findViewById(R.id.guestButton);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(LoginActivity.this, "게스트로 시작합니다.", Toast.LENGTH_SHORT);
                toast.show();

                Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }
}
