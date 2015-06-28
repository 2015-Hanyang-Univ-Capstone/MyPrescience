package com.myprescience.ui.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

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
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_SONG_COUNT;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.USER_API;
import static com.myprescience.util.Server.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.Server.WIDTH_150;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;


public class LoginActivity extends FragmentActivity {
    private MainFragment mainFragment;
    private Button guestButton;
    private UserData userDTO;
    private Boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = false;
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
                        initSetting(user);
                    }
                });
            } else {
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
                                    initSetting(user);
                                }
                            });
                        }
                    }
                });
            }
        }

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

    private void viewFadeIn(View layout) {
        Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.abc_fade_in);
        layout.startAnimation(animation);
        layout.setVisibility(View.VISIBLE);
    }

    public void initSetting(GraphUser user) {
        if(!login) {
            login = true;
            new insertUserTask().execute(SERVER_ADDRESS + USER_API + INSERT_FACEBOOK_ID + user.getId());
            new searchUserTask().execute(SERVER_ADDRESS + USER_API + USER_ID_WITH_FACEBOOK_ID + user.getId());
            new LoadProfileImage().execute(FACEBOOK_PROFILE + user.getId() + WIDTH_150);
            userDTO.setName(user.getName());
        }
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
                Log.e("user_id", userDTO.getId() + " is login." );
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            new selectSongCountTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_COUNT + WITH_USER + userDTO.getId());
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

    class selectSongCountTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... url) {
            String userIdJSON = getStringFromUrl(url[0]);
            JSONParser jsonParser = new JSONParser();
            JSONArray users = null;
            try {
                users = (JSONArray) jsonParser.parse(userIdJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            int songCount = 0;
            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                songCount = Integer.parseInt((String)user.get("song_count"));
            }
            return songCount;
        }

        @Override
        protected void onPostExecute(Integer song_count) {
            Log.e("song_count", song_count + "");
            userDTO.setRatingSongCount(song_count);

            if(song_count == 0) {
                DialogPrivacy().show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    public AlertDialog DialogPrivacy() {
        final TextView message = new TextView(LoginActivity.this);
        message.setText(getString(R.string.privacy_explain));
        message.setTextColor(getResources().getColor(R.color.button_material_light));
        message.setPadding(50, 50, 50, 50);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse(getString(R.string.privacy_url));
                i.setData(u);
                startActivity(i);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(getString(R.string.privacy_title))
                .setView(message)
                .setCancelable(true)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(getString(R.string.privacy_agree),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                })
                .setNegativeButton(getString(R.string.privacy_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                });


        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

}
