package com.myprescience.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.util.Indicator;
import com.myprescience.util.JSON;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {
    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;
//    public static String BBT_API = "http://166.104.245.89/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
//    public static String BBT_API = "http://218.37.215.185/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
    public static String BBT_API = "http://172.200.152.155:8888/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
    private String spotifyAPI = "https://api.spotify.com/v1/";

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

        Intent intent = getIntent();
        ArrayList<String> selectGenre = intent.getExtras().getStringArrayList("selectGenre");
        String genres = TextUtils.join(",", selectGenre);
        new getSimpleSongTask().execute(BBT_API+genres);
    }

    class getSimpleSongTask extends AsyncTask<String, String, String> {

        public getSimpleSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return mJson.getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);

                for(int i = 0; i < 10; i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String id = (String)song.get("id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");

                    final String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                    Bitmap albumArt = null;
                    if(!spotifyAlbumID.equals("albums/"))
                        try {
                            albumArt = new getAlbumTask().execute(spotifyAPI+spotifyAlbumID).get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    songListAdapter.addItem(id, albumArt, title, artist, 0);
                }
                songListAdapter.notifyDataSetChanged();

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

    /* Album JSON
       Parameter - String(url)
       Callback - genres, images */
    class getAlbumTask extends AsyncTask<String, String, Bitmap> {

        public getAlbumTask(){
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            String spotifyAlbumJSON = mJson.getStringFromUrl(url[0]);

            Log.e("url", url[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyAlbumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.e("album", album.toString());

            JSONArray images = (JSONArray) album.get("images");
            JSONObject image = (JSONObject) images.get(2);
            // Image 역시 UI Thread에서 바로 작업 불가.
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL((String)image.get("url"));
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
