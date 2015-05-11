package com.myprescience.ui.artist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
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


public class ArtistActivity extends Activity {

    String spotifyAPI = "https://api.spotify.com/v1/";
    int size640x640 = 0;
    int size300x300 = 1;
    int size64x64 = 2;

    Indicator mIndicator;
    Server json;
    ImageView artistImageView;
    TextView nameTextView, genresTextview, followersTextView;
    ProgressBar popularityProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        mIndicator = new Indicator(this);
        json = new Server();

        artistImageView = (ImageView) findViewById(R.id.artistImageView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        genresTextview = (TextView) findViewById(R.id.genresTextView);
        followersTextView = (TextView) findViewById(R.id.followersTextView);
        popularityProgressBar = (ProgressBar) findViewById(R.id.popularityProgressBar);

        Intent intent = getIntent();
        String spotifyArtistID = intent.getExtras().getString("spotifyArtistID");
        new getArtistTask().execute(spotifyAPI+spotifyArtistID);
    }

    class getArtistTask extends AsyncTask<String, String, String> {

        public getArtistTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return json.getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyTrackJSON) {
            super.onPostExecute(spotifyTrackJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject artist = null;
            try {
                artist = (JSONObject) jsonParser.parse(spotifyTrackJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JSONArray images = (JSONArray) artist.get("images");
            if(images.size() != 0) {
                JSONObject image = (JSONObject) images.get(size640x640);
                // Image 역시 UI Thread에서 바로 작업 불가.
                new ImageLoadTask((String) image.get("url"), artistImageView).execute();
            }

            nameTextView.setText( (String)artist.get("name") );

            JSONArray genres = (JSONArray) artist.get("genres");

            String genre = "";
            if(genres.size() != 0) {
                for(int i = 0; i < genres.size()-1; i++) {
                    genre += genres.get(i) + ", ";
                }
                genre += genres.get(genres.size()-1);
            }
            genresTextview.setText(genre);

            JSONObject followers = (JSONObject) artist.get("followers");
            followersTextView.setText( followers.get("total")+"" );

            long popularity = (long)artist.get("popularity");
            popularityProgressBar.setProgress((int)popularity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
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
