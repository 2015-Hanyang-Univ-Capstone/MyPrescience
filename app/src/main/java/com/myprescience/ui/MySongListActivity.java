package com.myprescience.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
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

import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.SPOTIFY_API;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.JSON.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MySongListActivity extends ActionBarActivity {

    private GridView gridView;
    private int userId;
    private MySongListAdapter mySongListAdapter;
    private boolean mLockListView = false;
    private int mListCount = 0;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView( R.layout.mysong_grid_view );

        Session session = Session.getActiveSession();
        if(session != null){
            if(session.isOpened()){
                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
                    // callback after Graph API response with user object
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        new searchUserTask().execute(SERVER_ADDRESS + USER_API + USER_ID_WITH_FACEBOOK_ID + user.getId());
                    }
                });
            }
        }

        mIndicator = new Indicator(this);

        gridView = (GridView) findViewById(R.id.gridView1);

        mySongListAdapter = new MySongListAdapter(this, userId);
        gridView.setAdapter(mySongListAdapter);

        new getSimpleSongTask().execute(SERVER_ADDRESS+"/rating.php?query=selectSongs&user_id=8");
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
            mLockListView = true;

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);

                // mListCount는 추가 로드할 때 마다 10씩 증가
                for(int i = mListCount; i < mListCount+songArray.size(); i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String id = (String)song.get("id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");
                    String rating = (String)song.get("rating")+"";

                    final String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                    Bitmap albumArt = null;
                    if(!spotifyAlbumID.equals("albums/")) {
                        try {
                            new addAlbumArtTask().execute(SPOTIFY_API + spotifyAlbumID, id, title, artist, rating);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
//                        songListAdapter.addItem(id, albumArt, title, artist, 0);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mLockListView = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
    }

    class addAlbumArtTask extends AsyncTask<String, String, Void> {

        public addAlbumArtTask(){
        }

        @Override
        protected Void doInBackground(String... parameter) {
            String spotifyAlbumJSON = getStringFromUrl(parameter[0]);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyAlbumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray images = (JSONArray) album.get("images");
            JSONObject image = (JSONObject) images.get(1);

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
            mySongListAdapter.addItem(parameter[1], myBitmap, parameter[2], parameter[3], Integer.parseInt(parameter[4]));

            if(mySongListAdapter.getCount() > 5) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mySongListAdapter.notifyDataSetChanged();
                    }
                });

                if ( mIndicator.isShowing())
                    mIndicator.hide();
            }

            return null;
        }

    }

}