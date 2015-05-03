package com.myprescience.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Rating;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.internal.e;
import com.myprescience.R;
import com.myprescience.dto.RecommendSongData;
import com.myprescience.util.InsertUpdateQuery;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.myprescience.util.PixelUtil.getProperImage;
import static com.myprescience.util.Server.GENRES_API;
import static com.myprescience.util.Server.INSERT_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_GENRE_WITH_DETAIL;
import static com.myprescience.util.Server.SELECT_SONG_AVG_RATING;
import static com.myprescience.util.Server.SELECT_SONG_RATING;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class RecommendSongListAdapter extends BaseAdapter {

    private int userId;
    private Context mContext = null;
    private ArrayList<RecommendSongData> mListData = new ArrayList<>();
    private ViewHolder holder;
    private float ACTIVE_NUM = (float) 0.75;

    public RecommendSongListAdapter(Context mContext, int _userId) {
        super();
        this.mContext = mContext;
        this.userId = _userId;
    }

    public void addItem(String _id, String _albumArtURL, String _title, String _artist, int _rating, String _genres, String _song_type,
                        float _valence, float _danceability, float _energy, float _liveness, float _speechiness, float _acousticness,
                        float _instrumentalness){
        RecommendSongData temp = new RecommendSongData();
        temp.id = _id;
        temp.title = _title;
        temp.artist = _artist;
        temp.rating = _rating;
        temp.albumUrl = _albumArtURL;
        temp.albumArt = null;
        temp.genres = _genres;
        temp.valenceProperty = _valence;
        temp.danceablilityProperty = _danceability;
        temp.energyProperty = _energy;
        temp.livenessProperty = _liveness;
        temp.speechinessProperty = _speechiness;
        temp.acousticProperty = _acousticness;
        temp.instrumentalnessProperty = _instrumentalness;
        temp.vocalProperty = false;
        temp.studioProperty = false;

        if(!_song_type.equals("")) {
            String[] song_types = _song_type.split(",");
            for(int i = 0; i < song_types.length; i++) {
                if (song_types[i].equals("vocal"))
                    temp.vocalProperty = true;
                if (song_types[i].equals("studio"))
                    temp.studioProperty = true;
            }
        }
        mListData.add(temp);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recommendsong, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.recommendAlbumArtImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.recommendTitleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.recommendArtistTextView);
            holder.ratingTextView = (TextView) convertView.findViewById(R.id.recommendRatingTextView);
            holder.genreTextView = (TextView) convertView.findViewById(R.id.recommendGenreTextView);

            holder.valenceButton = (Button) convertView.findViewById(R.id.recommendValenceButton);
            holder.speechinessButton = (Button) convertView.findViewById(R.id.recommendSpeechinessButton);
            holder.energyButton = (Button) convertView.findViewById(R.id.recommendEnergyButton);
            holder.studioButton = (Button) convertView.findViewById(R.id.recommendStudioButton);
            holder.danceablilityButton = (Button) convertView.findViewById(R.id.recommendAcousticButton);
            holder.acousticButton = (Button) convertView.findViewById(R.id.recommendAcousticButton);
            holder.instrumentalnessButton = (Button) convertView.findViewById(R.id.recommendInstrumentalnessButton);
            holder.vocalButton = (Button) convertView.findViewById(R.id.recommendVocalButton);
            holder.livenessButton = (Button) convertView.findViewById(R.id.recommendLivenessButton);

            holder.songDetailTextView = (TextView) convertView.findViewById(R.id.recommendSongDetailTextView);
            holder.insertRatingTextView = (TextView) convertView.findViewById(R.id.recommendInsertRatingTextView);
            holder.ratingRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.recommendRatingRelativeLayout);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final RecommendSongData mData = mListData.get(position);

        holder.titleTextView.setText(mData.title);
        holder.artistTextView.setText(mData.artist);

        float rating = mData.rating;
        if(rating < 100) {
            rating = (float) (mData.rating/20.0);
            holder.ratingTextView.setText(String.format("%.1f", rating));
        } else {
            holder.ratingTextView.setText(5.0+"");
        }
        holder.position = position;

        // 앨범아트 가져오기
        // Spotify에 앨범아트 정보가 있을 경우
        if(!(mData.albumUrl).equals("albums/")) {
            if(mData.albumArt == null) {
                holder.albumImageView.setImageResource(R.drawable.image_loading);
                try {
                    new LoadAlbumArt(position, holder, mData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SPOTIFY_API + mData.albumUrl);
                } catch (Exception e) { e.printStackTrace(); }
            }
            else
                holder.albumImageView.setImageBitmap(mData.albumArt);

        } else {
            holder.albumImageView.setImageResource(R.drawable.image_not_exist_300);
        }
        holder.albumImageView.setAdjustViewBounds(true);
//        holder.ratingBar.setTag(position);

        String[] genres = mData.genres.split(",");
        if(genres.length != 0 && mData.genre.equals(""))
            try {
                new extractGenreWithDetail(position, holder, mData).execute(SERVER_ADDRESS + GENRES_API + SELECT_GENRE_WITH_DETAIL + URLEncoder.encode(genres[0], "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        else
            holder.genreTextView.setText(mData.genre);

        if(mData.valenceProperty > ACTIVE_NUM)
            activePropertyButton(holder.valenceButton);
        else
            notActivePropertyButton(holder.valenceButton);

        if(mData.speechinessProperty > ACTIVE_NUM)
            activePropertyButton(holder.speechinessButton);
        else
            notActivePropertyButton(holder.speechinessButton);

        if(mData.energyProperty > ACTIVE_NUM)
            activePropertyButton(holder.energyButton);
        else
            notActivePropertyButton(holder.energyButton);

        if(mData.danceablilityProperty > ACTIVE_NUM)
            activePropertyButton(holder.danceablilityButton);
        else
            notActivePropertyButton(holder.danceablilityButton);

        if(mData.acousticProperty > ACTIVE_NUM)
            activePropertyButton(holder.acousticButton);
        else
            notActivePropertyButton(holder.acousticButton);

        if(mData.instrumentalnessProperty > ACTIVE_NUM)
            activePropertyButton(holder.instrumentalnessButton);
        else
            notActivePropertyButton(holder.instrumentalnessButton);

        if(mData.livenessProperty > ACTIVE_NUM)
            activePropertyButton(holder.livenessButton);
        else
            notActivePropertyButton(holder.livenessButton);

        if(mData.vocalProperty)
            activePropertyButton(holder.vocalButton);
        else
            notActivePropertyButton(holder.vocalButton);

        if(mData.studioProperty)
            activePropertyButton(holder.studioButton);
        else
            notActivePropertyButton(holder.studioButton);

        holder.songDetailTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SongActivity.class);
                intent.putExtra("song_id", mData.id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);
            }
        });

        holder.insertRatingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FrameLayout rootLayout = (FrameLayout) v.getParent().getParent().getParent().getParent();
                final RelativeLayout ratingLayout = (RelativeLayout) rootLayout.getChildAt(2);

                Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
                ratingLayout.setVisibility(View.VISIBLE);
                ratingLayout.startAnimation(fadeInAnimation);

                LinearLayout linearLayout = (LinearLayout) ratingLayout.getChildAt(0);
                holder.ratingBar = (RatingBar) linearLayout.getChildAt(1);
                holder.closeButton = (Button) linearLayout.getChildAt(2);

                holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                        int ratingInt = (int)(rating*2);
                        new InsertUpdateQuery().execute(SERVER_ADDRESS + RATING_API + INSERT_RATING +
                                "user_id=" + userId + "&song_id=" + mData.id + "&rating=" + ratingInt);
                        Toast toast = Toast.makeText(mContext, rating+"/5.0점으로 평가되었습니다!", Toast.LENGTH_SHORT);
                        toast.show();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                holder.closeButton.callOnClick();
                                holder.ratingBar.setOnRatingBarChangeListener(null);
                                holder.ratingBar.setProgress(0);
                            }
                        }, 1000);
                    }
                });

                holder.closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Animation fadeOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_out);
                        ratingLayout.setVisibility(View.GONE);
                        ratingLayout.startAnimation(fadeOutAnimation);
                    }
                });
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public RecommendSongData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class LoadAlbumArt extends AsyncTask<String, String, Bitmap> {

        private int mPosition;
        private ViewHolder mHolder = null;
        private RecommendSongData songData;

        public LoadAlbumArt(int positon, ViewHolder holder, RecommendSongData mSongData){
            this.mPosition = positon;
            this.mHolder = holder;
            this.songData = mSongData;
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
            JSONObject image = getProperImage(images, mHolder.albumImageView.getWidth());

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
        protected void onPostExecute(Bitmap mAlbumArt) {
            super.onPostExecute(mAlbumArt);
            songData.setAlbumArt(mAlbumArt);
            if (mHolder.position == mPosition) {
                mHolder.albumImageView.setImageBitmap(mAlbumArt);
            }
        }
    }

    class extractGenreWithDetail extends AsyncTask<String, String, String> {

        private int mPosition;
        private ViewHolder mHolder = null;
        private RecommendSongData songData;
        public String mGenre;

        public extractGenreWithDetail(int positon, ViewHolder holder, RecommendSongData mSongData){
            this.mPosition = positon;
            this.mHolder = holder;
            this.songData = mSongData;
            this.mGenre = songData.getGenre();
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String genreJSON) {
            super.onPostExecute(genreJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray genres = null;
            try {
                genres = (JSONArray) jsonParser.parse(genreJSON);
                if(genres.size() != 0) {
                    JSONObject genre = (JSONObject) genres.get(0);
                    String genreStr = (String) genre.get("genre");
                    if (!genre.equals("") && !checkDuplicate(genreStr))
                        mGenre += genreStr + " ";
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            songData.setGenre(mGenre);
            if (mHolder.position == mPosition) {
                mHolder.genreTextView.setText(mGenre);
            }
        }

        private boolean checkDuplicate(String inputGenre) {
            if(!mGenre.equals("")) {
                String[] genres = mGenre.split(" ");
                for (int i = 0; i < genres.length; i++) {
                    if (genres[i].equals(inputGenre))
                        return true;
                }
            }
            return false;
        }
    }

    public void activePropertyButton(Button button) {
        button.setBackgroundResource(R.drawable.rectangle_active);
    }

    public void notActivePropertyButton(Button button) {
        button.setBackgroundResource(R.drawable.rectangle_not_active);
    }

    public class ViewHolder {
        public ImageView albumImageView;
        public TextView titleTextView;
        public TextView artistTextView;
        public TextView ratingTextView;
        public int position;
        public TextView genreTextView;
        public Button valenceButton;
        public Button speechinessButton;
        public Button energyButton;
        public Button studioButton;
        public Button danceablilityButton;
        public Button acousticButton;
        public Button instrumentalnessButton;
        public Button vocalButton;
        public Button livenessButton;
        public TextView songDetailTextView;
        public TextView insertRatingTextView;
        public RelativeLayout ratingRelativeLayout;
        public RatingBar ratingBar;
        public Button closeButton;
    }



}
