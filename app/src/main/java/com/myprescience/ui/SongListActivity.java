package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.myprescience.R;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.myprescience.util.JSON.BBT_WITH_GENRE;
import static com.myprescience.util.JSON.BILLBOARDTOP_API;
import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.SPOTIFY_API;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.JSON.getStringFromUrl;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {
    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;
//    public static String BBT_API = "http://166.104.245.89/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
//    public static String BBT_API = "http://218.37.215.185/MyPrescience/db/BillboardTop.php?query=selectGenreTop&genres=";
    public static String BBT_API = SERVER_ADDRESS+BILLBOARDTOP_API+BBT_WITH_GENRE;
    public static int userId;

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

        Session session = Session.getActiveSession();
        if(session != null){
            if(session.isOpened()){
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        new searchUserTask().execute(SERVER_ADDRESS+USER_API+USER_ID_WITH_FACEBOOK_ID+user.getId());
                    }
                });
            }
        }

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
        songListAdapter = new SongListAdapter(this, selectCount, progressBar, textView, rightButton, userId);
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
            return getStringFromUrl(url[0]);
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
                            albumArt = new getAlbumTask().execute(SPOTIFY_API+spotifyAlbumID).get();
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
                userId = Integer.parseInt((String)user.get("user_id"));
            }
            return null;
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
            String spotifyAlbumJSON = getStringFromUrl(url[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyAlbumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

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
