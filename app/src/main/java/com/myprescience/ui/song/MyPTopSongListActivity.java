package com.myprescience.ui.song;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.myprescience.util.Server.MYP_TOP100_SONGS;
import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.RECOMMEND_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

@SuppressLint("ValidFragment")
public class MyPTopSongListActivity extends ActionBarActivity {

    private UserData userDTO;

    private ListView mMyPTopListView;
    private MyPTopSongListAdapter mMyPTopSongListAdapter;

    private Indicator mIndicator;

    private int totalListSize;
    private int mListCount;
    private int mListAddCount;
    private JSONArray mSongArray;
    private boolean mLockListView = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myptop100_song_list);
        userDTO = new UserData(getApplicationContext());
        setActionBar(R.string.title_activity_MyPTopSongListactivty);

        mListCount = 0;
        mListAddCount = 5;

        mIndicator = new Indicator(this);

        mMyPTopListView = (ListView) findViewById(R.id.mypTopSongListView);
        mMyPTopSongListAdapter = new MyPTopSongListAdapter(getApplicationContext(), userDTO.getId());
        mMyPTopListView.setAdapter(mMyPTopSongListAdapter);
        new getRecommendSongTask().execute(SERVER_ADDRESS+SONG_API+MYP_TOP100_SONGS+WITH_USER+userDTO.getId());

        // 스크롤 했을 때 마지막 셀이 보인다면 추가로 로딩
        mMyPTopListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
                // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정
                if ( (totalItemCount+mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount)
                        && (mLockListView == false) && (totalItemCount > 0) ) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    new getRecommendSongTask().execute();
                    mLockListView = true;
                } else if(totalItemCount+mListAddCount > totalListSize && totalListSize > 9) {
                    mListCount += mListAddCount;
                    mListAddCount =  totalListSize - mListCount;
                    new getRecommendSongTask().execute();
                    mMyPTopListView.setOnScrollListener(null);
                }
            }
        });
    }

    private void setActionBar(int title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        // 뒤로가기 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
        TitleTextView.setText(title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

    }

    class getRecommendSongTask extends AsyncTask<String, String, String> {

        public getRecommendSongTask() {
        }

        @Override
        protected String doInBackground(String... url) {
            if(mSongArray == null)
                return getStringFromUrl(url[0]);
            return null;
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                if(songJSON != null) {
                    mSongArray = (JSONArray) jsonParser.parse(songJSON);
                    totalListSize = mSongArray.size();
                }

                // mListCount는 추가 로드할 때 마다 10씩 증가
                for (int i = mListCount; i < mListCount + mListAddCount; i++) {
                    JSONObject song = (JSONObject) jsonParser.parse(mSongArray.get(i).toString());

                    String id = (String) song.get("id");
                    String spotifyArtistID = (String) song.get("artist_spotify_id");
                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    String spotifyAlbumID = "albums/" + (String) song.get("album_spotify_id");
                    String genres = (String) song.get("genres");
                    String song_type = (String) song.get("song_type");
                    String user_ratingStr = (String) song.get("user_rating");
                    String avg_ratingStr = (String) song.get("avg_rating");
                    int user_rating = 0;
                    float avg_rating = 0;
                    if (user_ratingStr != null)
                        user_rating = Integer.parseInt(user_ratingStr);
                    if (avg_ratingStr != null)
                        avg_rating = Float.parseFloat(avg_ratingStr);

                    float valence = Float.parseFloat((String) song.get("valence"));
                    float danceability = Float.parseFloat((String) song.get("danceability"));
                    float energy = Float.parseFloat((String) song.get("energy"));
                    float liveness = Float.parseFloat((String) song.get("liveness"));
                    float speechiness = Float.parseFloat((String) song.get("speechiness"));
                    float acousticness = Float.parseFloat((String) song.get("acousticness"));
                    float instrumentalness = 0;
                    if(song.get("instrumentalness") != null)
                        instrumentalness = Float.parseFloat((String) song.get("instrumentalness"));

                    mMyPTopSongListAdapter.addItem(id, spotifyArtistID, spotifyAlbumID, title, artist, user_rating, avg_rating, genres,
                            song_type, valence, danceability, energy, liveness, speechiness, acousticness, instrumentalness);
                }
                if (mMyPTopSongListAdapter.getCount() > 4) {
                    mMyPTopSongListAdapter.notifyDataSetChanged();
                }
                if (mIndicator.isShowing())
                    mIndicator.hide();

                mLockListView = false;
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

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
}