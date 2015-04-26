package com.myprescience.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.util.ChromeClient;
import com.myprescience.util.ErrorMsg;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_SONG_RATING;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SONG_WITH_ID;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.VIDEO_MOST_VIEW;
import static com.myprescience.util.Server.VIDEO_SMALL;
import static com.myprescience.util.Server.YOUTUBE_API;
import static com.myprescience.util.Server.YOUTUBE_EMBED;
import static com.myprescience.util.Server.YOUTUBE_RESULT_ONE;
import static com.myprescience.util.Server.YOUTUBE_API_KEY;
import static com.myprescience.util.Server.getStringFromUrl;


public class SongActivity extends Activity {

    String SONG_URL = SERVER_ADDRESS+SONG_API+SONG_WITH_ID;
    String SONG_ID;

    Indicator mIndicator;
    ErrorMsg mErrorMsg;

    private boolean finishPreview, finishImageLoad;
    private ScrollView scrollView;
    private ImageView albumArtView;
    private TextView titleTextView, artistTextView, genreTextView, ratingTextView, ratindCountTextView, songTypeTextView,
                        songModeTextView, songKeyTextView, tempoTextView, timeSignatureTextView, durationTextView,
                        albumNameTextView, trackNumTextView;
    private RatingBar ratingBar;
    private ProgressBar valanceProgressBar, loudnessProgressBar, danceablilityProgressBar, energyProgressBar,
                        livenessProgressBar, speechinessProgressBar, acousticnessProgressBar, instrumentalnessProgressBar,
                        popularityProgressBar;
    private LinearLayout mArtistButton, mAlbumButton;
    private ImageButton previewButton;
    private MediaPlayer mPlayer;
    private WebView mYoutubeMVWebView, mYoutubeLyricsWebView, mYoutubeLiveWebView;

    private String[] modes = {"Minor", "Major"};
    private String[] keys = {"C", "C#", "D", "E♭", "E", "F", "F#", "G", "A♭", "A", "B♭", "B"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        Intent intent = getIntent();
        SONG_ID = intent.getExtras().getString("song_id");

        mIndicator = new Indicator(this);
        mErrorMsg = new ErrorMsg();

        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0, 0);

        albumArtView = (ImageView) findViewById(R.id.albumArtView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        artistTextView = (TextView) findViewById(R.id.artistTextView);
        genreTextView = (TextView) findViewById(R.id.genreTextView);
        ratingTextView = (TextView) findViewById(R.id.ratingTextView);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

        ratindCountTextView = (TextView) findViewById(R.id.ratingCountTextView);
        albumNameTextView = (TextView) findViewById(R.id.albumNameTextView);
        trackNumTextView = (TextView) findViewById(R.id.trackNumTextView);
        popularityProgressBar = (ProgressBar) findViewById(R.id.popularityProgressBar);

        mArtistButton = (LinearLayout) findViewById(R.id.artistButton);
        mAlbumButton = (LinearLayout) findViewById(R.id.albumButton);

        songTypeTextView = (TextView) findViewById(R.id.songTypeTextView);
        songModeTextView = (TextView) findViewById(R.id.songModeTextView);
        songKeyTextView = (TextView) findViewById(R.id.songKeyTextView);
        tempoTextView = (TextView) findViewById(R.id.tempoTextView);
        timeSignatureTextView = (TextView) findViewById(R.id.timeSignatureTextView);
        durationTextView = (TextView) findViewById(R.id.durationTextView);

        valanceProgressBar = (ProgressBar) findViewById(R.id.valanceProgressBar);
        loudnessProgressBar = (ProgressBar) findViewById(R.id.loudnessProgressBar);
        danceablilityProgressBar = (ProgressBar) findViewById(R.id.danceablilityProgressBar);
        energyProgressBar = (ProgressBar) findViewById(R.id.energyProgressBar);
        livenessProgressBar = (ProgressBar) findViewById(R.id.livenessProgressBar);
        speechinessProgressBar = (ProgressBar) findViewById(R.id.speechinessProgressBar);
        acousticnessProgressBar = (ProgressBar) findViewById(R.id.acousticnessProgressBar);
        instrumentalnessProgressBar = (ProgressBar) findViewById(R.id.instrumentalnessProgressBar);

        previewButton = (ImageButton) findViewById(R.id.previewButton);

        mYoutubeMVWebView = (WebView) findViewById(R.id.youtubeMVWebView);
        mYoutubeLyricsWebView = (WebView) findViewById(R.id.youtubeLyricsWebView);
        mYoutubeLiveWebView = (WebView) findViewById(R.id.youtubeLiveWebView);

        new getSongTask().execute(SONG_URL+SONG_ID);

        settingWebView(mYoutubeMVWebView);
        settingWebView(mYoutubeLyricsWebView);
        settingWebView(mYoutubeLiveWebView);

//        mYoutubeVideoView.loadUrl("http://www.youtube.com/embed/" + "60A_f8clKog" + "?autoplay=1&vq=small");
        // YouTube Search에서 끝에 concert를 더하고, JSON Parsing해서 VideoId만 가져오기.
    }

    public void settingWebView(WebView webview) {
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setPluginState(WebSettings.PluginState.ON);
        webview.getSettings().setSupportMultipleWindows(true);

        webview.setWebChromeClient(new ChromeClient(this));
        webview.setWebViewClient(new WebViewClient());
    }

    // 초 -> 00 : 00
    public String convertMS(int duration) {
        int M = duration / 60;
        int S = duration % 60;
        return String.format("%02d", M) + " : " + String.format("%02d", S);
    }

    class getRatingTask extends AsyncTask<String, String, String> {

        public getRatingTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String resultJSON) {
            super.onPostExecute(resultJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray response = null;
            try {
                response = (JSONArray) jsonParser.parse(resultJSON);
                if(response.size() != 0) {
                    JSONObject rating = (JSONObject) response.get(0);
                    if(rating.get("avg") != null) {
                        float avg = Float.parseFloat((String) rating.get("avg"));
                        int avg_rating = Math.round(avg);
                        int rating_count = Integer.parseInt((String) rating.get("rating_count"));

                        ratingTextView.setText(String.format("(%.1f)", avg_rating / 2.0));
                        ratingBar.setProgress(avg_rating);
                        ratindCountTextView.setText(rating_count + "명이\n이 노래를\n평가했습니다!");
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    /* Album JSON
       Parameter - String(url)
       Callback - genres, images */
    class getAlbumTask extends AsyncTask<String, String, String> {

        public getAlbumTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyAlbumJSON) {
            super.onPostExecute(spotifyAlbumJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject album = null;
            try {
                album = (JSONObject) jsonParser.parse(spotifyAlbumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray genres = (JSONArray) album.get("genres");

            String genre = "";
            if(genres.size() != 0) {
                for(int i = 0; i < genres.size()-1; i++) {
                    genre += genres.get(i) + ", ";
                }
                genre += genres.get(genres.size()-1);
            }
            genreTextView.setText(genre);

            JSONArray images = (JSONArray) album.get("images");
            JSONObject image = (JSONObject) images.get(1);
            // Image 역시 UI Thread에서 바로 작업 불가.
            new ImageLoadTask((String)image.get("url"), albumArtView).execute();
            albumNameTextView.setText((String)album.get("name"));
        }
    }

    /* Track JSON
       Parameter - String(url)
       Callback - popularity, track_number, preview */
    class getTrackTask extends AsyncTask<String, String, String> {

        public getTrackTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String spotifyTrackJSON) {
            super.onPostExecute(spotifyTrackJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject track = null;
            try {
                track = (JSONObject) jsonParser.parse(spotifyTrackJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            long popularity = (long) track.get("popularity");
            popularityProgressBar.setProgress((int) popularity);

            trackNumTextView.setText("Track." + track.get("track_number"));
            mPlayer = new MediaPlayer();

            String preview = (String) track.get("preview_url");
            if(preview != null) {
                new setSourceTask(mPlayer).execute(preview);
            } else {
                previewButton.setImageResource(R.drawable.icon_x_mark);

                if ( mIndicator.isShowing())
                    mIndicator.hide();
            }

        }

    }

    /* set Preview Source
       Parameter - String(url)  */
    class setSourceTask extends AsyncTask<String, String, Void> {

        private MediaPlayer mPlayer;

        public setSourceTask(MediaPlayer mPlayer){
            this.mPlayer = mPlayer;
        }

        @Override
        protected Void doInBackground(String... url) {
            try {
                mPlayer.setDataSource(url[0]);
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    previewButton.setImageResource(R.drawable.button_play);
                }
            });

            previewButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                        previewButton.setImageResource(R.drawable.icon_pause);
                    } else {
                        mPlayer.pause();
                        previewButton.setImageResource(R.drawable.icon_play);
                    }
                }
            });

            if(mIndicator.isShowing())
                mIndicator.hide();
        }
    }

    /* Song JSON
       Parameter - String(url)
       Callback - song detail,
                  spotifyTrackID, spotifyArtistID, spotifyAlbumID (Async Task) */
    class getSongTask extends AsyncTask<String, String, String> {

        public getSongTask(){
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
                JSONObject song = (JSONObject) jsonParser.parse(songArray.get(0).toString());

                String title = (String)song.get("title");
                String artist = (String)song.get("artist");
                String songType = (String)song.get("song_type");

                double tempo = Math.round(Double.parseDouble((String) song.get("tempo")) * 100) / 100.0;
                int timeSignature = Integer.parseInt((String)song.get("time_signature"));
                double duration = Math.round(Double.parseDouble((String)song.get("duration"))*100) / 100.0;
                int songMode = Integer.parseInt((String)song.get("song_mode"));
                int songKey = Integer.parseInt((String)song.get("song_key"));

                int valence = Math.round(Float.parseFloat((String)song.get("valence"))*100);
                int loudness = Math.round(Float.parseFloat((String)song.get("loudness"))*100);
                int danceability = Math.round(Float.parseFloat((String)song.get("danceability"))*100);
                int energy = Math.round(Float.parseFloat((String)song.get("energy"))*100);
                int liveness = Math.round(Float.parseFloat((String)song.get("liveness"))*100);
                int speechiness = Math.round(Float.parseFloat((String)song.get("speechiness"))*100);
                int acousticness = Math.round(Float.parseFloat((String)song.get("acousticness"))*100);
                int instrumentalness = Math.round(Float.parseFloat((String)song.get("instrumentalness"))*100);

                titleTextView.setText(title);
                artistTextView.setText(artist);
                songTypeTextView.setText(songType);

                tempoTextView.setText(Double.toString(tempo) + " bpm");
                timeSignatureTextView.setText(timeSignature+"");
                durationTextView.setText(convertMS((int)duration));
                songModeTextView.setText(modes[songMode]);
                songKeyTextView.setText(keys[songKey]);

                valanceProgressBar.setProgress(valence);
                loudnessProgressBar.setProgress(loudness);
                danceablilityProgressBar.setProgress(danceability);
                energyProgressBar.setProgress(energy);
                livenessProgressBar.setProgress(liveness);
                speechinessProgressBar.setProgress(speechiness);
                acousticnessProgressBar.setProgress(acousticness);
                instrumentalnessProgressBar.setProgress(instrumentalness);

                String spotifyTrackID = "tracks/"+(String)song.get("track_spotify_id");
                if(spotifyTrackID.equals("tracks/")) {
                    trackNumTextView.setText(mErrorMsg.NOT_FOUND);
                    popularityProgressBar.setProgress(0);
                    previewButton.setImageResource(R.drawable.icon_x_mark);
                    finishPreview = true;
                } else {
                    new getTrackTask().execute(SPOTIFY_API+spotifyTrackID);
                }

                final String spotifyArtistID = "artists/"+(String)song.get("artist_spotify_id");
//                String spotifyArtistJSON = new getArtistTask().execute(spotifyAPI+spotifyArtistID).get();

                mArtistButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(spotifyArtistID.equals("artists/")) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SongActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage("아티스트에 대한 정보가 없습니다.");
                            alert.show();
                        } else {
                            Intent intent = new Intent(SongActivity.this, ArtistActivity.class);
                            intent.putExtra("spotifyArtistID", spotifyArtistID);
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    }
                });

                final String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                if(spotifyAlbumID.equals("albums/")) {
                    genreTextView.setText(mErrorMsg.NOT_FOUND);
                    albumNameTextView.setText(mErrorMsg.NOT_FOUND);
                    albumArtView.setImageResource(R.drawable.icon_none);

                    mAlbumButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(SongActivity.this);
                            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();     //닫기
                                }
                            });
                            alert.setMessage("앨범에 대한 정보가 없습니다.");
                            alert.show();
                        }
                    });

                    if (finishPreview && mIndicator.isShowing())
                        mIndicator.hide();
                } else {
                    new getAlbumTask().execute(SPOTIFY_API+spotifyAlbumID);

                    mAlbumButton.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(SongActivity.this, AlbumActivity.class);
                            intent.putExtra("spotifyAlbumID", spotifyAlbumID );
                            startActivity(intent);
                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        }
                    });
                }

                new getRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_RATING + SONG_ID);

                String keyword = title+" "+artist;
                new setYouTubeTask(mYoutubeMVWebView).execute(YOUTUBE_API + URLEncoder.encode(keyword, "utf-8") + YOUTUBE_RESULT_ONE+YOUTUBE_API_KEY+VIDEO_MOST_VIEW);
                new setYouTubeTask(mYoutubeLyricsWebView).execute(YOUTUBE_API + URLEncoder.encode(keyword + " lyrics", "utf-8") + YOUTUBE_RESULT_ONE+YOUTUBE_API_KEY);
                new setYouTubeTask(mYoutubeLiveWebView).execute(YOUTUBE_API + URLEncoder.encode(keyword + " concert live", "utf-8") + YOUTUBE_RESULT_ONE+YOUTUBE_API_KEY);

            } catch (ParseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
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

    /* Image Load - ImageView
       Parameter - String(url), ImageView
       Callback - Image.set*/
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
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            finishImageLoad = true;
            if (finishPreview && mIndicator.isShowing())
                mIndicator.hide();
        }
    }

    class setYouTubeTask extends AsyncTask<String, String, String> {

        private WebView mWebView;

        public setYouTubeTask(WebView _webview){
            this.mWebView = _webview;
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String youtubeJSON) {
            super.onPostExecute(youtubeJSON);

            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject youtube = (JSONObject) jsonParser.parse(youtubeJSON);
                JSONArray items = (JSONArray) youtube.get("items");
                JSONObject id = null;
                if(items.size()!= 0) {
                    id = (JSONObject) ((JSONObject) items.get(0)).get("id");
                    String video_id = (String) id.get("videoId");
                    mWebView.loadUrl(YOUTUBE_EMBED + video_id);
                } else {
                    mWebView.loadUrl(null);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
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
