package com.myprescience.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.myprescience.util.Server.INSERT_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 리스트 뷰에 곡 정보를 추가하기 위한 어댑터 클래스
 * addItem()으로 리스트에 곡 추가
 */

public class SongListAdapter extends BaseAdapter{

    private int userId;
    private Context mContext = null;
    private ArrayList<SongData> mListData = new ArrayList<>();
    private ViewHolder holder;
    private ProgressBar progressBar;
    private int ratingCount;
    private TextView textView;
    private ImageButton rightButton;

    public SongListAdapter(Context mContext, int _ratingCount, ProgressBar _progressBar, TextView _textView, ImageButton _rightButton, int _userId) {
        super();
        this.mContext = mContext;
        progressBar = _progressBar;
        ratingCount = _ratingCount;
        textView = _textView;
        rightButton = _rightButton;
        this.userId = _userId;
    }

    public void addItem(String _id, String _album, String _title, String _artist, int _rating){
        SongData temp = new SongData();
        temp.id = _id;
        temp.albumArt = _album;
        temp.title = _title;
        temp.artist = _artist;
        temp.rating = _rating;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song_list_item, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.albumArtView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

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
        holder.ratingBar.setProgress(mData.rating);

        holder.position = position;

        if(!(mData.albumArt).equals("albums/")) {
            try {
                new LoadAlbumArt(position, holder).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, SPOTIFY_API+mData.albumArt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            holder.albumImageView.setImageResource(R.drawable.icon_none);
        }
        holder.ratingBar.setTag(position);

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!fromUser) return;

                int index = (Integer)ratingBar.getTag();
                if(mListData.get(index).rating == 0) {
                    ratingCount++;
                    setProgress(ratingCount);

//                    textView.setText(ratingCount + "곡을 평가했습니다.");
                    textView.invalidate();

                    if(ratingCount >= SongListActivity.MIN_SELECTED_SONG)
                        rightButton.setVisibility(ImageButton.VISIBLE);
                }

                mListData.get(index).rating = (int)(rating*2);

                new insertRatingTask().execute(SERVER_ADDRESS+RATING_API+INSERT_RATING+
                        "user_id=" + userId + "&song_id=" + mListData.get(index).id + "&rating=" + mListData.get(index).rating);

                Toast toast = Toast.makeText(mContext, rating+"/5.0점으로 평가되었습니다!", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        return convertView;
    }

    public void setProgress(int count) {
        progressBar.setProgress((int)(Math.min(1, count/(double)SongListActivity.MIN_SELECTED_SONG)*100));
        progressBar.invalidate();
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

        public LoadAlbumArt(int positon, ViewHolder holder){
            this.mPosition = positon;
            this.mHolder = holder;
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
            Bitmap myBitmap = null;
            if(images.size() != 0) {
                JSONObject image = (JSONObject) images.get(2);
                // Image 역시 UI Thread에서 바로 작업 불가.
                try {
                    URL urlConnection = new URL((String) image.get("url"));
                    HttpURLConnection connection = (HttpURLConnection) urlConnection
                            .openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap = BitmapFactory.decodeStream(input);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap albumArt) {
            super.onPostExecute(albumArt);
            if (mHolder.position == mPosition) {
                mHolder.albumImageView.setImageBitmap(albumArt);
            }
        }
    }
}
