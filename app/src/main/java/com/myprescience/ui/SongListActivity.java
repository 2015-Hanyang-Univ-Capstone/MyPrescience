package com.myprescience.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.util.Indicator;
import com.myprescience.util.JSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {
    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;
    public static String TEST_URL = "http://166.104.245.89/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=pop,rock,hiphop";
    private JSON mJson = new JSON();
    private Indicator mIndicator;

    private int selectCount = 0;
    private ImageButton rightButton;
    private TextView textView;
    private ListView songListView;
    private SongListAdapter songListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        mIndicator = new Indicator(this);

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSonglistActivity = SongListActivity.this;
                Intent intent = new Intent(SongListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.top);
        textView.setText("최소 "+ MIN_SELECTED_SONG +"곡 이상 평가해주세요.");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        songListAdapter = new SongListAdapter(this, selectCount, progressBar, textView, rightButton);
        songListView = (ListView) findViewById(R.id.songListView);
        songListView.setAdapter(songListAdapter);

        for(int i=1; i<4; i++)
            songListAdapter.addItem(null,i+"번째 곡 타이틀",i+"번째 곡 아티스트",0);

        new getSongTask().execute(TEST_URL);
    }

    class getSongTask extends AsyncTask<String, String, String> {

        public getSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return mJson.getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);
            Log.e("songJSON", songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);

                for(int i = 0; i < 5; i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");

                    songListAdapter.addItem(null, title, artist, 0);
                }
//                    String spotifyTrackID = "tracks/"+(String)song.get("track_spotify_id");
//                    if(spotifyTrackID.equals("tracks/")) {
//                        trackNumTextView.setText(mErrorMsg.NOT_FOUND);
//                        popularityProgressBar.setProgress(0);
//                    } else {
//                        new getTrackTask().execute(spotifyAPI+spotifyTrackID);
//                    }

                if ( mIndicator.isShowing())
                    mIndicator.hide();

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                if ( !mIndicator.isShowing())
                    mIndicator.show();
        }
    }
}
