package com.myprescience.ui.mix_play;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.ErrorMsg;
import com.myprescience.util.InsertUpdateQuery;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.myprescience.util.Server.INSERT_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_SONG_AVG_RATING;
import static com.myprescience.util.Server.SELECT_SONG_RATING;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SONG_WITH_ID;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;


public class SongFragment extends Fragment {

    public static String SPOTIFY_ARTIST_ID;
    public static String SPOTIFY_ALBUM_ID;

    private static final String LIST_FRAGMENT_TAG = "song_fragment";

    private UserData userDTO;

    String SONG_URL = SERVER_ADDRESS+SONG_API+SONG_WITH_ID;
    String SONG_ID;

//    Indicator mIndicator;
    ErrorMsg mErrorMsg;

//    private Activity mActivity = this;

    private ScrollView scrollView;
    private ImageView albumArtView;
    private TextView titleTextView, artistTextView, genreTextView, ratingTextView, ratindCountTextView,
                        songModeTextView, songKeyTextView, tempoTextView, timeSignatureTextView, durationTextView,
                        albumNameTextView, trackNumTextView, userRatingTextView;
    private TextView valanceTextview, danceablilityTextview, energyTextview,
                        livenessTextview, speechinessTextview, acousticnessTextview, instrumentalnessTextview;
    private TextView songType1Textview, songType2Textview, songType3Textview, songType4Textview, previewTextview;
    private RatingBar mSongActivityRatingBar;
    private ProgressBar popularityProgressBar;
    private LinearLayout mArtistButton, mAlbumButton, mEvaluationButton, mPreviewClick;
    private ImageButton previewButton;
    private MediaPlayer mPlayer;
    private RelativeLayout mRatingRelativeLayout;
    private Button mCloseButton;

    private String[] modes = {"Minor", "Major"};
    private String[] keys = {"C", "C#", "D", "E♭", "E", "F", "F#", "G", "A♭", "A", "B♭", "B"};

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root_view = inflater.inflate(R.layout.fragment_song, container, false);
        userDTO = new UserData(getActivity());

        if(getActivity().getIntent().getExtras().getString("song_id") != null)
            SONG_ID = getActivity().getIntent().getExtras().getString("song_id");

//        mIndicator = new Indicator(getActivity());
        mErrorMsg = new ErrorMsg();

        scrollView = (ScrollView) root_view.findViewById(R.id.scrollView);
        scrollView.scrollTo(0, 0);

        albumArtView = (ImageView) root_view.findViewById(R.id.albumArtView);

        userRatingTextView = (TextView) root_view.findViewById(R.id.userRatingTextView);
        titleTextView = (TextView) root_view.findViewById(R.id.titleTextView);
        artistTextView = (TextView) root_view.findViewById(R.id.artistTextView);
        genreTextView = (TextView) root_view.findViewById(R.id.genreTextView);
        ratingTextView = (TextView) root_view.findViewById(R.id.ratingTextView);

        mSongActivityRatingBar = (RatingBar) root_view.findViewById(R.id.songActivityRatingBar);
        LayerDrawable stars = (LayerDrawable) mSongActivityRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.color_base_theme), PorterDuff.Mode.SRC_ATOP);

        ratindCountTextView = (TextView) root_view.findViewById(R.id.ratingCountTextView);
        albumNameTextView = (TextView) root_view.findViewById(R.id.albumNameTextView);
        trackNumTextView = (TextView) root_view.findViewById(R.id.trackNumTextView);
        popularityProgressBar = (ProgressBar) root_view.findViewById(R.id.popularityProgressBar);

        mArtistButton = (LinearLayout) root_view.findViewById(R.id.artistButton);
        mAlbumButton = (LinearLayout) root_view.findViewById(R.id.albumButton);
        mEvaluationButton = (LinearLayout) root_view.findViewById(R.id.evaluationButton);

        songModeTextView = (TextView) root_view.findViewById(R.id.songModeTextView);
        songKeyTextView = (TextView) root_view.findViewById(R.id.songKeyTextView);
        tempoTextView = (TextView) root_view.findViewById(R.id.tempoTextView);
        timeSignatureTextView = (TextView) root_view.findViewById(R.id.timeSignatureTextView);
        durationTextView = (TextView) root_view.findViewById(R.id.durationTextView);

        valanceTextview= (TextView) root_view.findViewById(R.id.valanceTextview);
        danceablilityTextview = (TextView) root_view.findViewById(R.id.danceablilityTextview);
        energyTextview = (TextView) root_view.findViewById(R.id.energyTextview);
        livenessTextview = (TextView) root_view.findViewById(R.id.livenessTextview);
        speechinessTextview = (TextView) root_view.findViewById(R.id.speechinessTextview);
        acousticnessTextview = (TextView) root_view.findViewById(R.id.acousticnessTextview);
        instrumentalnessTextview = (TextView) root_view.findViewById(R.id.instrumentalnessTextview);

        songType1Textview = (TextView) root_view.findViewById(R.id.songType1Textview);
        songType2Textview = (TextView) root_view.findViewById(R.id.songType2Textview);
        songType3Textview = (TextView) root_view.findViewById(R.id.songType3Textview);
        songType4Textview = (TextView) root_view.findViewById(R.id.songType4Textview);
        previewTextview = (TextView) root_view.findViewById(R.id.previeTextview);

        mPreviewClick = (LinearLayout) root_view.findViewById(R.id.previewClick);
        previewButton = (ImageButton) root_view.findViewById(R.id.previewButton);

        mRatingRelativeLayout = (RelativeLayout) root_view.findViewById(R.id.ratingRelativeLayout);
        mCloseButton = (Button) root_view.findViewById(R.id.closeButton);

        if(SONG_ID != null) {
            try {
                new getSongTask().execute(SONG_URL + SONG_ID);
            } catch (Exception e) {
                titleTextView.setText("음악정보를");
                artistTextView.setText("불러오는 중");
                albumNameTextView.setText("오류가");
                trackNumTextView.setText("발생하였습니다.");
                e.printStackTrace();
            }
        }

        return root_view;
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
                    if(rating.get("rating") != null) {
                        int user_rating = Integer.parseInt((String) rating.get("rating"));
                        if(user_rating > 10)
                            userRatingTextView.setText("보유곡");
                        else
                            userRatingTextView.setText(String.format("%.1f", user_rating / 2.0));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }

    class getAvgRatingTask extends AsyncTask<String, String, String> {

        public getAvgRatingTask(){
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
                        ratingTextView.setText(String.format("%.1f", avg / 2.0));

                        int rating_count = Integer.parseInt((String) rating.get("rating_count"));
                        ratindCountTextView.setText(rating_count + "명이 이 노래를 평가했습니다!");
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
//                new setSourceTask(mPlayer).execute(preview);
            } else {
                previewButton.setImageResource(R.drawable.icon_x_mark);
                previewTextview.setText("Preview ");
                mPreviewClick.setVisibility(View.VISIBLE);
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
                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.prepareAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    previewTextview.setText("Preview ");
                    previewButton.setImageResource(R.drawable.icon_play);

                    mPreviewClick.setVisibility(View.VISIBLE);
                    mPreviewClick.setOnClickListener(new OnClickListener() {
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
                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    previewButton.setImageResource(R.drawable.icon_play);
                }
            });
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

                if(song != null) {

                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    String songType = (String) song.get("song_type");

                    double tempo = Math.round(Double.parseDouble((String) song.get("tempo")) * 100) / 100.0;
                    int timeSignature = Integer.parseInt((String) song.get("time_signature"));
                    double duration = Math.round(Double.parseDouble((String) song.get("duration")) * 100) / 100.0;
                    int songMode = Integer.parseInt((String) song.get("song_mode"));
                    int songKey = Integer.parseInt((String) song.get("song_key"));

                    float valence = (float) 0.5;
                    if ((String) song.get("valence") != null)
                        valence = Float.parseFloat((String) song.get("valence"));


                    float danceability = (float) 0.5;
                    if ((String) song.get("danceability") != null)
                        danceability = Float.parseFloat((String) song.get("danceability"));

                    float energy = (float) 0.5;
                    if ((String) song.get("energy") != null)
                        energy = Float.parseFloat((String) song.get("energy"));

                    float liveness = (float) 0.5;
                    if ((String) song.get("liveness") != null)
                        liveness = Float.parseFloat((String) song.get("liveness"));

                    float speechiness = (float) 0.5;
                    if ((String) song.get("speechiness") != null)
                        speechiness = Float.parseFloat((String) song.get("speechiness"));

                    float acousticness = (float) 0.5;
                    if ((String) song.get("acousticness") != null)
                        acousticness = Float.parseFloat((String) song.get("acousticness"));

                    float instrumentalness = (float) 0.5;
                    if ((String) song.get("instrumentalness") != null)
                        instrumentalness = Float.parseFloat((String) song.get("instrumentalness"));

                    titleTextView.setText(title);
                    artistTextView.setText(artist);
//                songTypeTextView.setText(songType);
                    String[] songTypes = songType.split(",");
                    for (int i = 0; i < songTypes.length; i++) {
                        if (songTypes[i].equals("studio"))
                            setActivePropertyView(songType1Textview, 3);
                        if (songTypes[i].equals("christmas"))
                            setActivePropertyView(songType2Textview, 3);
                        if (songTypes[i].equals("electric"))
                            setActivePropertyView(songType3Textview, 3);
                        if (songTypes[i].equals("vocal"))
                            setActivePropertyView(songType4Textview, 3);
                    }

                    tempoTextView.setText(Double.toString(tempo) + " bpm");
                    timeSignatureTextView.setText(timeSignature + " 박자");
                    durationTextView.setText(convertMS((int) duration));
                    songModeTextView.setText(modes[songMode]);
                    songKeyTextView.setText(keys[songKey]);

                    setSongPropertyText(valanceTextview, valence);
                    setSongPropertyText(danceablilityTextview, danceability);
                    setSongPropertyText(energyTextview, energy);
                    setSongPropertyText(livenessTextview, liveness);
                    setSongPropertyText(speechinessTextview, speechiness);
                    setSongPropertyText(acousticnessTextview, acousticness);
                    setSongPropertyText(instrumentalnessTextview, instrumentalness);

                    String spotifyTrackID = "tracks/" + (String) song.get("track_spotify_id");
                    if (spotifyTrackID.equals("tracks/")) {
                        trackNumTextView.setText(mErrorMsg.NOT_FOUND);
                        popularityProgressBar.setProgress(0);
                        previewButton.setImageResource(R.drawable.icon_x_mark);
                        previewTextview.setText("Preview ");
                        mPreviewClick.setVisibility(View.VISIBLE);
                    } else {
                        new getTrackTask().execute(SPOTIFY_API + spotifyTrackID);
                    }

                    final String spotifyArtistID = "artists/" + (String) song.get("artist_spotify_id");
                    SPOTIFY_ARTIST_ID = spotifyArtistID;
//                String spotifyArtistJSON = new getArtistTask().execute(spotifyAPI+spotifyArtistID).get();

                    mArtistButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (spotifyArtistID.equals("artists/")) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();     //닫기
                                    }
                                });
                                alert.setMessage("아티스트에 대한 정보가 없습니다.");
                                alert.show();
                            } else {
                                getArtistFragment(spotifyArtistID);
//                            Intent intent = new Intent(SongFragment.this, ArtistActivity.class);
//                            intent.putExtra("spotifyArtistID", spotifyArtistID);
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            }
                        }
                    });

                    final String spotifyAlbumID = "albums/" + (String) song.get("album_spotify_id");
                    SPOTIFY_ALBUM_ID = spotifyAlbumID;
                    if (spotifyAlbumID.equals("albums/")) {
                        genreTextView.setText(mErrorMsg.NOT_FOUND);
                        albumNameTextView.setText(mErrorMsg.NOT_FOUND);
                        albumArtView.setImageResource(R.drawable.image_not_exist_600);

                        mAlbumButton.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
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
//                    mIndicator.hide();
                    } else {
                        new getAlbumTask().execute(SPOTIFY_API + spotifyAlbumID);

                        mAlbumButton.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                getAlbumFragment(spotifyAlbumID);
//                            Intent intent = new Intent(SongFragment.this, AlbumActivity.class);
//                            intent.putExtra("spotifyAlbumID", spotifyAlbumID );
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                            }
                        });
                    }

                    new getRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_RATING + SONG_ID + WITH_USER + userDTO.getId());
                    new getAvgRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_AVG_RATING + SONG_ID);

                    mEvaluationButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_in);
                            mRatingRelativeLayout.setVisibility(View.VISIBLE);
                            mRatingRelativeLayout.startAnimation(fadeInAnimation);
                        }
                    });

                    mSongActivityRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            int ratingInt = (int) (rating * 2);
                            new InsertUpdateQuery(getActivity()).execute(SERVER_ADDRESS + RATING_API + INSERT_RATING +
                                    "user_id=" + userDTO.getId() + "&song_id=" + SONG_ID + "&rating=" + ratingInt +
                                    "&artist_id=" + spotifyArtistID + "&album_id=" + spotifyAlbumID.substring(7));
                            Toast toast = Toast.makeText(getActivity(), rating + "/5.0점으로 평가되었습니다!", Toast.LENGTH_SHORT);
                            toast.show();

                            userDTO.addRatingSoungCount(SONG_ID, ratingInt);

                            new getRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_RATING + SONG_ID + WITH_USER + userDTO.getId());
                            new getAvgRatingTask().execute(SERVER_ADDRESS + RATING_API + SELECT_SONG_AVG_RATING + SONG_ID);

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    mCloseButton.callOnClick();
                                }
                            }, 1000);
                        }
                    });

                    mCloseButton.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.abc_fade_out);
                            mRatingRelativeLayout.setVisibility(View.GONE);
                            mRatingRelativeLayout.startAnimation(fadeOutAnimation);
                        }
                    });
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if ( !mIndicator.isShowing())
//                mIndicator.show();
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
//            if (mIndicator.isShowing())
//                mIndicator.hide();
        }
    }

    private void setSongPropertyText(TextView propertyTextView, float property) {

        int px = 0;
        Log.e("Tag", (String) propertyTextView.getTag());
        String[] propertyText = ((String) propertyTextView.getTag()).split("#");

        if(property >= 0.5) {
            if (property >= 0.8) {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Large);
                px = 10;
            } else if (property >= 0.6 && property < 0.8) {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
                px = 5;
            } else {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Small);
                px = 1;
            }
            propertyTextView.setText(propertyText[0]);
            setActiveView(propertyTextView, px);
        } else {
            if(property < 0.2) {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Large);
                px = 10;
            } else if(property >= 0.2 && property < 0.4) {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Medium);
                px = 5;
            } else {
                propertyTextView.setTextAppearance(getActivity(), android.R.style.TextAppearance_DeviceDefault_Small);
                px = 1;
            }
            propertyTextView.setText(propertyText[1]);
            setNotActiveView(propertyTextView, px);
        }
        propertyTextView.setTextColor(Color.parseColor("#FFFFFF"));
    }

    private void setActiveView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.button_active_background);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    private void setActivePropertyView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.rectangle_active);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    private void setNotActiveView(View view, int px) {
        int bottom = view.getPaddingBottom();
        int top = view.getPaddingTop();
        int right = view.getPaddingRight();
        int left = view.getPaddingLeft();
        view.setBackgroundResource(R.drawable.button_property2_background);
        view.setPadding(left + px, top + px, right + px, bottom + px);
    }

    private void getArtistFragment(String artist_id) {

        getActivity().getIntent().putExtra("spotifyArtistID", artist_id);

        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down)
                .add(R.id.filterFragment_Container, ArtistFragment
                                .instantiate(getActivity(), ArtistFragment.class.getName()),
                        LIST_FRAGMENT_TAG
                ).addToBackStack(null).commit();
    }

    private void getAlbumFragment(String album_id) {

        getActivity().getIntent().putExtra("spotifyAlbumID", album_id);

        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down)
                .add(R.id.filterFragment_Container, AlbumFragment
                                .instantiate(getActivity(), AlbumFragment.class.getName()),
                        LIST_FRAGMENT_TAG
                ).addToBackStack(null).commit();
    }

}
