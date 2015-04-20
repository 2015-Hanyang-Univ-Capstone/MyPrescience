package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.RECOMMEND_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.USER_ID;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

import com.myprescience.R;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressLint("ValidFragment")
public class RecommendTab extends Fragment {

    Context mContext;

    private ListView mRecommendListView;
    private RecommendSongListAdapter mRecommendSongListAdapter;

    private Indicator mIndicator;

    private int totalListSize;
    private int mListCount;
    private int mListAddCount;

    public RecommendTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_recommend, null);

        mListCount = 0;
        mListAddCount = 5;

        mIndicator = new Indicator(mContext, view);

        mRecommendListView = (ListView) view.findViewById(R.id.recommendSongListView);
        mRecommendSongListAdapter = new RecommendSongListAdapter(mContext, USER_ID);
        mRecommendListView.setAdapter(mRecommendSongListAdapter);

        new getRecommendSongTask().execute(SERVER_ADDRESS+RECOMMEND_API+RECOMMEND_SONGS+WITH_USER+USER_ID);

//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
//        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);

        return view;
    }

    class getRecommendSongTask extends AsyncTask<String, String, String> {

        public getRecommendSongTask() {
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                Log.e("songJSON", songJSON);
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);
                totalListSize = songArray.size();

                // mListCount는 추가 로드할 때 마다 10씩 증가
                for (int i = mListCount; i < mListCount + mListAddCount; i++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String id = (String) song.get("id");
                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    String spotifyAlbumID = "albums/" + (String) song.get("album_spotify_id");
                    String ratingStr = (String) song.get("rating");
                    int rating = 0;
                    if (ratingStr != null) {
                        rating = Integer.parseInt(ratingStr);
                    }
                    mRecommendSongListAdapter.addItem(id, spotifyAlbumID, title, artist, rating);

                    if (mRecommendSongListAdapter.getCount() > 4) {
                        mRecommendSongListAdapter.notifyDataSetChanged();

                        if (mIndicator.isShowing())
                            mIndicator.hide();
                    }
                }

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
}