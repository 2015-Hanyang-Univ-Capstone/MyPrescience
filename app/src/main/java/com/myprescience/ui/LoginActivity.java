package com.myprescience.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.myprescience.R;
import com.myprescience.dto.UserData;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import static com.myprescience.util.Server.FACEBOOK_PROFILE;
import static com.myprescience.util.Server.INSERT_FACEBOOK_ID;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.USER_API;
import static com.myprescience.util.Server.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.Server.WIDTH_150;
import static com.myprescience.util.Server.getStringFromUrl;


public class LoginActivity extends FragmentActivity {
    private MainFragment mainFragment;
    private Button guestButton;
    private UserData userDTO;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDTO = new UserData(getApplicationContext());

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

        // 현제 페이스북 로그인 세션 확인
        LoginButton authButton = (LoginButton) findViewById(R.id.authButton);
        Session session = Session.getActiveSession();
        if(session != null){
            if(session.isOpened()){
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
//                        Toast.makeText(LoginActivity.this, user.getName()+"님 환영합니다!", Toast.LENGTH_SHORT).show();
                        initSetting(user);
//                        Toast.makeText(LoginActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(LoginActivity.this, user.getName()+"님 환영합니다!", Toast.LENGTH_SHORT).show();
                            initSetting(user);
//                            Toast.makeText(LoginActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
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

    public void initSetting(GraphUser user) {
        new insertUserTask().execute(SERVER_ADDRESS+USER_API+INSERT_FACEBOOK_ID+user.getId());
        new searchUserTask().execute(SERVER_ADDRESS+USER_API+USER_ID_WITH_FACEBOOK_ID+user.getId());
        new LoadProfileImage().execute(FACEBOOK_PROFILE+user.getId()+WIDTH_150);
        userDTO.setName(user.getName());
    }

    class searchUserTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... url) {
            String userIdJSON = getStringFromUrl(url[0]);
            JSONParser jsonParser = new JSONParser();
            JSONArray users = null;
            try {
                users = (JSONArray) jsonParser.parse(userIdJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                userDTO.setId(Integer.parseInt((String) user.get("user_id")));
                Log.e("user_id", userDTO.getId() + "is login." );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    class insertUserTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }
    }

    class LoadProfileImage extends AsyncTask<String, String, Bitmap> {

        public LoadProfileImage() {
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return myBitmap;
        }
        @Override
        protected void onPostExecute(Bitmap albumArt) {
            super.onPostExecute(albumArt);
            userDTO.setFacebook_profile(albumArt);

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
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
