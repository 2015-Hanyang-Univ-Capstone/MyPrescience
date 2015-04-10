package com.myprescience.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.myprescience.R;
import com.myprescience.util.JSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.myprescience.util.JSON.INSERT_FACEBOOK_ID;
import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.getStringFromUrl;


public class LoginActivity extends FragmentActivity {
    private MainFragment mainFragment;
    private Button guestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new MainFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, mainFragment)
                    .commit();
        } else {
            // Or set the fragment from restored state info
            mainFragment = (MainFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }
        setContentView(R.layout.activity_login);


        // 현제 페이스북 로그인 세션 확인
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        Session session = Session.getActiveSession();
        if(session != null){
            if(session.isOpened()){
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        Toast.makeText(LoginActivity.this, user.getName()+"님 환영합니다!", Toast.LENGTH_LONG).show();

                        new insertUserTask().execute(SERVER_ADDRESS+USER_API+INSERT_FACEBOOK_ID+user.getId());

//                        Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }

        // 페이스북 로그인
        authButton.setReadPermissions(Arrays.asList("public_profile"));
        authButton.setSessionStatusCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                if (session.isOpened()) {
                    Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                        // callback after Graph API response with user object
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            Toast.makeText(LoginActivity.this, user.getName()+"님 환영합니다!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        // 임시용 게스트 로그인 버튼
//        guestButton = (Button) findViewById(R.id.guestButton);
//        guestButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast toast = Toast.makeText(LoginActivity.this, "게스트로 시작합니다.", Toast.LENGTH_SHORT);
//                toast.show();
//
//                Intent intent = new Intent(LoginActivity.this, RecommendActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
    }

    class insertUserTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }
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
