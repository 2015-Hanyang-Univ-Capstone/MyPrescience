package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;
import com.myprescience.util.RangeSeekBar;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.myprescience.util.Server.CLAUSE;
import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.RECOMMEND_SEARCH_SONGS;
import static com.myprescience.util.Server.RECOMMEND_SEARCH_SONGS_WITH_GENRE;
import static com.myprescience.util.Server.RECOMMEND_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;

@SuppressLint("ValidFragment")
public class SearchSongTab extends Fragment {

    Context mContext;
    ScrollView mSearchLayout;
    ViewGroup mStep1TableLayout, mStep2TableLayout, mStep3TableLayout;
    TextView mStep2TextView, mStep3TextView;
    Button mSearchButton, mResearchButton;

    ArrayList<String> songs, genres, properties;
    private UserData userDTO = new UserData();

    private FrameLayout mRecommendSearchLayout;
    private ListView mRecommendListView;
    private RecommendSongListAdapter mRecommendSongListAdapter;

    private Indicator mIndicator;

    private int totalListSize;
    private int mListCount;
    private int mListAddCount;
    private JSONArray mSongArray;
    private boolean mLockListView = false;


    public SearchSongTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_search, null);
        initSetting(view);

        mListCount = 0;
        mListAddCount = 5;

        mSearchLayout = (ScrollView) view.findViewById(R.id.searchLayout);
        mRecommendSearchLayout = (FrameLayout) view.findViewById(R.id.recommendSearchLayout);
        mIndicator = new Indicator(mContext, mRecommendSearchLayout);

        mRecommendListView = (ListView) view.findViewById(R.id.recommendSearchSongListView);
        mRecommendSongListAdapter = new RecommendSongListAdapter(mContext, userDTO.getId());
        mRecommendListView.setAdapter(mRecommendSongListAdapter);

        // 스크롤 했을 때 마지막 셀이 보인다면 추가로 로딩
        mRecommendListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    mRecommendListView.setOnScrollListener(null);
                }
            }
        });

        mSearchButton = (Button) view.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SONGS", TextUtils.join(" AND ", songs));
                Log.e("GENRES", TextUtils.join(" AND ", genres));
                Log.e("PROPERTY", TextUtils.join(" AND ", properties));

                if(!songs.get(0).equals("all"))
                    properties.addAll(songs);

                if(properties.size() != 0 && genres.size() == 0)
                    new getRecommendSongTask().execute(SERVER_ADDRESS+RECOMMEND_API+RECOMMEND_SEARCH_SONGS+WITH_USER+userDTO.getId()
                            +CLAUSE+ TextUtils.join("%20AND%20", properties));
                else if(properties.size() != 0 && genres.size() != 0)
                    new getRecommendSongTask().execute(SERVER_ADDRESS + RECOMMEND_API + RECOMMEND_SEARCH_SONGS_WITH_GENRE+
                            TextUtils.join("%20AND%20", genres) + WITH_USER + userDTO.getId()
                            + CLAUSE + TextUtils.join("%20AND%20", properties));

                Log.e("Query", SERVER_ADDRESS + RECOMMEND_API + RECOMMEND_SEARCH_SONGS_WITH_GENRE+
                        TextUtils.join("%20OR%20", genres) + WITH_USER + userDTO.getId()
                        + CLAUSE + TextUtils.join("%20AND%20", properties));

                viewFadeOut(mSearchLayout);
                viewFadeIn(mRecommendListView);
                viewFadeIn(mResearchButton);

                properties.removeAll(songs);
            }
        });

        mResearchButton = (Button) view.findViewById(R.id.reSearchButton);
        mResearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFadeOut(mRecommendListView);
                viewFadeOut(mResearchButton);
                viewFadeIn(mSearchLayout);

                initSongList();
                mRecommendSongListAdapter = new RecommendSongListAdapter(mContext, userDTO.getId());
                mRecommendListView.setAdapter(mRecommendSongListAdapter);
                mRecommendSongListAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void initSongList() {
        mListCount = 0;
        mListAddCount = 5;
        mSongArray = null;
    }

    private void initSetting(View view) {

        mStep1TableLayout = (ViewGroup) view.findViewById(R.id.step1TableLayout);
        mStep2TableLayout = (ViewGroup) view.findViewById(R.id.step2TableLayout);
        mStep3TableLayout = (ViewGroup) view.findViewById(R.id.step3TableLayout);

        mStep2TextView = (TextView) view.findViewById(R.id.step2TextView);
        mStep3TextView = (TextView) view.findViewById(R.id.step3TextView);

        songs = new ArrayList<String>();
        genres = new ArrayList<String>();
        properties = new ArrayList<String>();

        for(int i = 0; i < mStep1TableLayout.getChildCount(); i ++) {
            final ViewGroup TableRow = (ViewGroup) mStep1TableLayout.getChildAt(i);
            for (int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            notActive(v, songs);
                        } else {
                            active(v, songs);
                            String tag = (String) v.getTag();
                            if((tag).equals("all"))
                                notActiveAllSongKind();
                            else
                                notActiveSongKindAll();
                        }
                        if(mStep2TableLayout.getVisibility() == View.GONE)
                            viewFadeIn(mStep2TableLayout, mStep2TextView);
                    }
                });
            }
        }



        for(int i = 0; i < mStep2TableLayout.getChildCount(); i ++) {
            ViewGroup TableRow = (ViewGroup) mStep2TableLayout.getChildAt(i);
            for (int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected())
                            notActive(v, genres);
                        else
                            active(v, genres);
                        if(mStep3TableLayout.getVisibility() == View.GONE)
                            viewFadeIn(mStep3TableLayout, mStep3TextView);
                    }
                });
            }
        }

        for(int i = 0; i < mStep3TableLayout.getChildCount(); i ++) {
            ViewGroup TableRow = (ViewGroup) mStep3TableLayout.getChildAt(i);
            for(int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected())
                            notActive(v, properties);
                        else
                            active(v, properties);
                    }
                });
            }
        }
    }

    private void viewFadeIn(View layout, View textView) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
        layout.startAnimation(fadeInAnimation);
        textView.startAnimation(fadeInAnimation);
        layout.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
    }

    private void viewFadeIn(View layout) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);
        layout.startAnimation(animation);
        layout.setVisibility(View.VISIBLE);
    }

    private void viewFadeOut(View layout) {
        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_out);
        layout.startAnimation(animation);
        layout.setVisibility(View.GONE);
    }

    private void active(View view, ArrayList step) {
        view.setBackgroundResource(R.drawable.rectangle_active);
        view.setSelected(true);
        try {
            step.add(URLEncoder.encode((String) view.getTag(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    private void notActive(View view, ArrayList step) {
        view.setBackgroundResource(R.drawable.rectangle_not_active);
        view.setSelected(false);
        try {
            step.remove(URLEncoder.encode((String) view.getTag(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void notActiveSongKindAll() {
        View view = ((TableRow) mStep1TableLayout.getChildAt(0)).getChildAt(0);
        view.setBackgroundResource(R.drawable.rectangle_not_active);
        view.setSelected(false);
        try {
            songs.remove(URLEncoder.encode((String) view.getTag(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void notActiveAllSongKind() {
        for(int i = 0; i < mStep1TableLayout.getChildCount(); i++) {
            TableRow tableRow = (TableRow) mStep1TableLayout.getChildAt(i);
            for(int j = 0; j < tableRow.getChildCount(); j++) {
                if(i == 0 && j == 0) {

                } else {
                    View view = tableRow.getChildAt(j);
                    notActive(view, songs);
                }
            }
        }
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

                if(totalListSize < 5)
                    mListAddCount = totalListSize;

                if(totalListSize != 0 ) {
                    // mListCount는 추가 로드할 때 마다 10씩 증가
                    for (int i = mListCount; i < mListCount + mListAddCount; i++) {
                        Log.e("sum", ""+mListCount + mListAddCount);
                        JSONObject song = (JSONObject) jsonParser.parse(mSongArray.get(i).toString());

                        String id = (String) song.get("id");
                        String title = (String) song.get("title");
                        String artist = (String) song.get("artist");
                        String spotifyAlbumID = "albums/" + (String) song.get("album_spotify_id");
                        String genres = (String) song.get("genres");
                        String song_type = (String) song.get("song_type");
                        String ratingStr = (String) song.get("rating");
                        int rating = 0;
                        if (ratingStr != null) {
                            rating = Integer.parseInt(ratingStr);
                        }

                        float valence = Float.parseFloat((String) song.get("valence"));
                        float danceability = Float.parseFloat((String) song.get("danceability"));
                        float energy = Float.parseFloat((String) song.get("energy"));
                        float liveness = Float.parseFloat((String) song.get("liveness"));
                        float speechiness = Float.parseFloat((String) song.get("speechiness"));
                        float acousticness = Float.parseFloat((String) song.get("acousticness"));
                        float instrumentalness = 0;
                        if (song.get("instrumentalness") != null)
                            instrumentalness = Float.parseFloat((String) song.get("instrumentalness"));
                        mRecommendSongListAdapter.addItem(id, spotifyAlbumID, title, artist, rating, genres, song_type,
                                valence, danceability, energy, liveness, speechiness, acousticness, instrumentalness);
                    }
                    mRecommendSongListAdapter.notifyDataSetChanged();
                } else {
                    mRecommendSongListAdapter.addItem("", "albums/", "노래가 없습니다.", "다시 검색해보세요!", 0, "", "",
                            0, 0, 0, 0, 0, 0, 0);
                    mRecommendSongListAdapter.notifyDataSetChanged();
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
}