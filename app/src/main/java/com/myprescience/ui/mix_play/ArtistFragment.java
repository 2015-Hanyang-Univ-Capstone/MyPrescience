package com.myprescience.ui.mix_play;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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


public class ArtistFragment extends Fragment {

    String spotifyAPI = "https://api.spotify.com/v1/";
    int size640x640 = 0;
    int size300x300 = 1;
    int size64x64 = 2;

    Server json;
    ImageView artistImageView;
    TextView nameTextView, genresTextview, followersTextView;
    ProgressBar popularityProgressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_artist, container, false);

        json = new Server();

        artistImageView = (ImageView) root_view.findViewById(R.id.artistImageView);
        nameTextView = (TextView) root_view.findViewById(R.id.nameTextView);
        genresTextview = (TextView) root_view.findViewById(R.id.genresTextView);
        followersTextView = (TextView) root_view.findViewById(R.id.followersTextView);
        popularityProgressBar = (ProgressBar) root_view.findViewById(R.id.popularityProgressBar);

        Intent intent = getActivity().getIntent();
        String spotifyArtistID = intent.getExtras().getString("spotifyArtistID");
        new getArtistTask().execute(spotifyAPI+spotifyArtistID);

        return root_view;
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
        }

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
