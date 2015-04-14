package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.util.Indicator;
import com.myprescience.util.Server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by dongjun on 15. 3. 27..
 */
public class AlbumActivity extends Activity{

    String mSpotifyAPI = "https://api.spotify.com/v1/";
    int mSize640x640 = 0;
    int mSize300x300 = 1;
    int mSize64x64 = 2;

    Indicator mIndicator;
    Server mJson;
    LinearLayout mTrackLinearLayout;
    ImageView mAlbumImageView;
    TextView mNameTextView, mGenresTextview, mFollowersTextView, mReleaseTextView, mCopyrightTextView;
    ProgressBar mPopularityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        mIndicator = new Indicator(this);
        mJson = new Server();

        mAlbumImageView = (ImageView) findViewById(R.id.albumImageView);
        mNameTextView = (TextView) findViewById(R.id.nameTextView);
        mGenresTextview = (TextView) findViewById(R.id.genresTextView);
        mReleaseTextView = (TextView) findViewById(R.id.releaseTextView);
        mCopyrightTextView = (TextView) findViewById(R.id.copyrightTextView);
        mFollowersTextView = (TextView) findViewById(R.id.followersTextView);
        mPopularityProgressBar = (ProgressBar) findViewById(R.id.popularityProgressBar);
        mTrackLinearLayout = (LinearLayout) findViewById(R.id.trackLinearLayout);

        Intent intent = getIntent();
        String spotifyAlbumID = intent.getExtras().getString("spotifyAlbumID");
        new getAlbumTask().execute(mSpotifyAPI+spotifyAlbumID);
    }

    class getAlbumTask extends AsyncTask<String, String, String> {

        public getAlbumTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return mJson.getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyTrackJSON) {
            super.onPostExecute(spotifyTrackJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyTrackJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray images = (JSONArray) album.get("images");
            JSONObject image = (JSONObject) images.get(mSize640x640);
            // Image 역시 UI Thread에서 바로 작업 불가.
            new ImageLoadTask((String)image.get("url"), mAlbumImageView).execute();

            mNameTextView.setText( (String)album.get("name") );

            JSONArray genres = (JSONArray) album.get("genres");
            String genre = "";
            if(genres.size() != 0) {
                for(int i = 0; i < genres.size()-1; i++) {
                    genre += genres.get(i) + ", ";
                }
                genre += genres.get(genres.size()-1);
            }
            mGenresTextview.setText(genre);

            String release = (String)album.get("release_date");
            mReleaseTextView.setText(release);

            JSONArray copyRights = (JSONArray) album.get("copyrights");
            String copyRightStr = "";
            if(copyRights.size() != 0) {
                JSONObject copyright = (JSONObject)copyRights.get(0);
                copyRightStr = (String)copyright.get("text");
            }
            mCopyrightTextView.setText(copyRightStr);

            long popularity = (long)album.get("popularity");
            mPopularityProgressBar.setProgress((int)popularity);

            JSONObject tracks = (JSONObject) album.get("tracks");
            JSONArray items = (JSONArray) tracks.get("items");
            if(items.size() != 0) {
                for(int i = 0; i < items.size(); i++) {
                    JSONObject item = (JSONObject) items.get(i);
                    long trackNumber = (long) item.get("track_number");
                    String name = (String) item.get("name");
                    Long duration_ms = (Long) item.get("duration_ms");
                    int duration = (int)(duration_ms/1000);
                    TextView mTrackTextView = new TextView(AlbumActivity.this);
                    mTrackTextView.setText((int)trackNumber + ". " + name +" (" + convertMS(duration) + ")");
                    mTrackTextView.setTextColor(Color.parseColor("#FFFFFF"));
                    mTrackLinearLayout.addView(mTrackTextView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
    }

    // 초 -> 00 : 00
    public String convertMS(int duration) {
        int M = duration / 60;
        int S = duration % 60;
        return String.format("%02d", M) + ":" + String.format("%02d", S);
    }

    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;
//        private Activity mActivity;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
            imageView.setAdjustViewBounds(true);
            if (mIndicator.isShowing())
                mIndicator.hide();
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
