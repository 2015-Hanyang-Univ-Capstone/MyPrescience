package com.myprescience.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.myprescience.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class RecommendSongListAdapter extends BaseAdapter {

    private int userId;
    private Context mContext = null;
    private ArrayList<SongData> mListData = new ArrayList<>();
    private ViewHolder holder;

    public RecommendSongListAdapter(Context mContext, int _userId) {
        super();
        this.mContext = mContext;
        this.userId = _userId;
    }

    public void addItem(String _id, String _albumArtURL, String _title, String _artist, int _rating){
        SongData temp = new SongData();
        temp.id = _id;
        temp.title = _title;
        temp.artist = _artist;
        temp.rating = _rating;
        temp.albumUrl = _albumArtURL;
        temp.albumArt = null;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_recommendsong, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.recommendAlbumArtImageView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.recommendTitleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.recommendArtistTextView);
            holder.ratingTextView = (TextView) convertView.findViewById(R.id.recommendRatingTextView);

//            LayerDrawable stars = (LayerDrawable) holder.ratingBar.getProgressDrawable();
//            stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final SongData mData = mListData.get(position);
        holder.albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SongActivity.class);
                intent.putExtra("song_id", mData.id);
                v.getContext().startActivity(intent);
            }
        });

        holder.titleTextView.setText(mData.title);
        holder.artistTextView.setText(mData.artist);

        float rating = Math.round(mData.rating/20.0);
        holder.ratingTextView.setText(String.format("%.1f", rating/10.0));
        holder.position = position;

        // 앨범아트 가져오기
        // Spotify에 앨범아트 정보가 있을 경우
        if(!(mData.albumUrl).equals("albums/")) {
            if(mData.albumArt == null) {
                holder.albumImageView.setImageResource(R.drawable.icon_loading);
                try {
                    new LoadAlbumArt(position, holder, mData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SPOTIFY_API + mData.albumUrl);
                } catch (Exception e) { e.printStackTrace(); }
            }
            else
                holder.albumImageView.setImageBitmap(mData.albumArt);

        } else {
            holder.albumImageView.setImageResource(R.drawable.icon_none);
        }
        holder.albumImageView.setAdjustViewBounds(true);
//        holder.ratingBar.setTag(position);

        return convertView;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public SongData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class insertRatingTask extends AsyncTask<String, String, Void> {

        @Override
        protected Void doInBackground(String... url) {
            String userIdJSON = getStringFromUrl(url[0]);
            Log.e("userIdJSON", userIdJSON);
            return null;
        }
    }

    class LoadAlbumArt extends AsyncTask<String, String, Bitmap> {

        private int mPosition;
        private ViewHolder mHolder = null;
        private SongData songData;

        public LoadAlbumArt(int positon, ViewHolder holder, SongData mSongData){
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
            JSONObject image = (JSONObject) images.get(0);

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

    public class ViewHolder {
        public ImageView albumImageView;
        public TextView titleTextView;
        public TextView artistTextView;
        public TextView ratingTextView;
        public int position;
    }



}
