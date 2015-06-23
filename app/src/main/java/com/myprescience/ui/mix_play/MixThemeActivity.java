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
import android.widget.RelativeLayout;
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
public class MixThemeActivity extends ActionBarActivity {

    private int MYRECOM = 0, RAN = 1;
    private String HAPPY = "happy", SAD = "sad", DANCE = "dance", COMFORT = "comfort", CHEERUP = "cheerup",
                RUN = "run", EMOTION = "emotion", LATEST = "latest", OLD = "old";

    private String[] playlist_id = new String[200];
    private String[] playlist = new String[200];

    private UserData userDTO;

    private RadioButton mMyRecomSongButton, mRanSongButton;
    private LinearLayout mThemeHappyButton, mThemeSadButton, mThemeDanceButton,
                        mThemeEmotionButton, mThemeComfortButton, mThemeNewSongButton,
                        mThemeCheerUpButton, mThemeRunButton, mThemeOldSongButton,
                        mThemeGenreButton, mThemeBillboardButton, mThemeMyPRecomButton;
    private RelativeLayout mPlayerGenreSelectLayout;

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
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    if(mMyRecomSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + EMOTION
                                + WITH_MODE + MYRECOM + WITH_USER + userDTO.getId());
                    else if(mRanSongButton.isChecked())
                        new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_THEME + EMOTION
                                + WITH_MODE + RAN + WITH_USER + userDTO.getId());
                }
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
                if(!mMyRecomSongButton.isChecked() && !mRanSongButton.isChecked()) {
                    showClickSongAlert();
                } else {
                    Intent intent = new Intent(MixThemeActivity.this, SelectGenreActivity.class);
                    if(mMyRecomSongButton.isChecked())
                        intent.putExtra("mode", MYRECOM);
                    else if(mRanSongButton.isChecked())
                        intent.putExtra("mode", RAN);
                    startActivity(intent);
                }
            }
        });

        mThemeBillboardButton = (LinearLayout) findViewById(R.id.themeBillboardButton);
        mThemeBillboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_MIX_BILLBOARD);
            }
        });

        mThemeMyPRecomButton = (LinearLayout) findViewById(R.id.themeMyPRecomButton);
        mThemeMyPRecomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playlist[0] = "Talking to the moon Bruno mars";
                playlist[1] = "Lay Me Down Sam Smith";
                playlist[2] = "Until It Gone Linkin Park";
                playlist[3] = "when i was your man Bruno mars";
                playlist[4] = "가슴 시린 이야기 휘성";
                playlist[5] = "독기 아이언";
                playlist[6] = "일리어네어 연결고리";
                playlist[7] = "불한당가 불한당가";
                playlist[8] = "AMOR FATI 에픽하이";
                playlist[9] = "독 이센스";
                playlist[10] = "진격의 거인 둘 다이나믹 듀오";
                playlist[11] = "The Show Must Go On Queen";
                playlist[12] = "Let it Go James Bay";
                playlist[13] = "Long Drive Jason Mraz";
                playlist[14] = "Lost In The Echo Linkin Park";
                playlist[15] = "Mad World Adam lambert";
                playlist[16] = "청춘연가 넬";
                playlist[17] = "Octagon 아웃사이더";
                playlist[18] = "map the soul 에픽하이";
                playlist[19] = "Sing Ed Sheeran";
                playlist[20] = "Smoke and Mirrors Imagine Dragons";
                playlist[21] = "Somebody to Love Queen";
                playlist[22] = "Someday 리쌍";
                playlist[23] = "Uptown Funk Mark Ronson";
                playlist[24] = "What I've Done Linkin Park";
                playlist[25] = "Whataya Want From Me Adam Lambert";
                playlist[26] = "Warriors Papa Roach";
                playlist[28] = "211 바스코";
                playlist[29] = "사랑놀이 MFBTY";
                playlist[30] = "시간과 낙엽 악동뮤지션";

                startMixPlay();
            }
        });
    }

    public void showClickSongAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MixThemeActivity.this);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage(getString(R.string.play_chose_mode));
        alert.show();
        return;
    }

    public void startMixPlay() {
        Intent intent = new Intent(MixThemeActivity.this, PlayerActivity.class);
        intent.putExtra("playlist", playlist);
        intent.putExtra("playlist_id", playlist_id);
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
                    String id = (String) song.get("id");
                    playlist_id[i] = id;

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
