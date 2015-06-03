package com.myprescience.ui.mix_play;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.myprescience.util.Server.MIX_PLAY_API;
import static com.myprescience.util.Server.SELECT_MIX_BILLBOARD;
import static com.myprescience.util.Server.SELECT_THEME;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_MODE;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MixPlayActivity extends ActionBarActivity {

    private int MYRECOM = 0, RAN = 1;
    private String HAPPY = "happy", SAD = "sad", DANCE = "dance", COMFORT = "comfort", CHEERUP = "cheerup", RUN = "run",
                LATEST = "latest", OLD = "old";

    private String[] playlist = new String[200];

    private UserData userDTO;

    private RadioButton mMyRecomSongButton, mRanSongButton;
    private LinearLayout mThemeHappyButton, mThemeSadButton, mThemeDanceButton,
                        mThemeEmotionButton, mThemeComfortButton, mThemeNewSongButton,
                        mThemeCheerUpButton, mThemeRunButton, mThemeOldSongButton,
                        mThemeGenreButton, mThemeBillboardButton, mThemeMyPRecomButton;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_play_theme);
        setActionBar(R.string.title_activity_MixPlay);
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);

        mMyRecomSongButton = (RadioButton) findViewById(R.id.myRecomSongButton);
        mRanSongButton = (RadioButton) findViewById(R.id.ranSongButton);

        mThemeHappyButton = (LinearLayout) findViewById(R.id.themeHappyButton);
        mThemeHappyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                showClickSongAlert();
            } else {
                if(mMyRecomSongButton.isChecked())
                    new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + HAPPY
                            + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                else if(mRanSongButton.isChecked())
                    new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + HAPPY
                            + WITH_MODE + RAN + WITH_USER + userDTO.getId());
            }
            }
        });

        mThemeSadButton = (LinearLayout) findViewById(R.id.themeSadButton);
        mThemeSadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + SAD
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + SAD
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeDanceButton = (LinearLayout) findViewById(R.id.themeDanceButton);
        mThemeDanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + DANCE
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + DANCE
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeEmotionButton = (LinearLayout) findViewById(R.id.themeEmotionButton);
        mThemeEmotionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mThemeComfortButton = (LinearLayout) findViewById(R.id.themeComfortButton);
        mThemeComfortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + COMFORT
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + COMFORT
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeNewSongButton = (LinearLayout) findViewById(R.id.themeNewSongButton);
        mThemeNewSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + LATEST
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + LATEST
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeCheerUpButton = (LinearLayout) findViewById(R.id.themeCheerUpButton);
        mThemeCheerUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + CHEERUP
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + CHEERUP
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeRunButton = (LinearLayout) findViewById(R.id.themeRunButton);
        mThemeRunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + RUN
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + RUN
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeOldSongButton = (LinearLayout) findViewById(R.id.themeOldSongButton);
        mThemeOldSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + OLD
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + OLD
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
            }
        });

        mThemeGenreButton = (LinearLayout) findViewById(R.id.themeGenreButton);
        mThemeGenreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        mThemeBillboardButton = (LinearLayout) findViewById(R.id.themeBillboardButton);
        mThemeBillboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_MIX_BILLBOARD);
                }
            }
        });

        mThemeMyPRecomButton = (LinearLayout) findViewById(R.id.themeMyPRecomButton);
        mThemeMyPRecomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    public void showClickSongAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MixPlayActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("음악종류를 먼저 선택해주세요!");
        alert.show();
        return;
    }

    public void startMixPlay() {
        Intent intent = new Intent(MixPlayActivity.this, PlayerActivity.class);
        intent.putExtra("playlist", playlist);
        startActivity(intent);
    }

    private void setActionBar(int title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        // 뒤로가기 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
        TitleTextView.setText(title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu., menu);
        return true;
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    class getPlayList extends AsyncTask<String, String, String> {

        public getPlayList(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray playlistJSON = (JSONArray) jsonParser.parse(songJSON);

                for(int i = 0; i < playlistJSON.size(); i ++) {
                    JSONObject song = (JSONObject) playlistJSON.get(i);
                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    playlist[i] = title + " " + artist;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mIndicator.isShowing())
                mIndicator.hide();
            startMixPlay();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
    }

}
