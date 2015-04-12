package com.myprescience.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.myprescience.R;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import static com.myprescience.util.JSON.BBT_WITH_GENRE;
import static com.myprescience.util.JSON.BILLBOARDTOP_API;
import static com.myprescience.util.JSON.RANDOM_SONGS;
import static com.myprescience.util.JSON.RATING_API;
import static com.myprescience.util.JSON.SELECT_SONG_COUNT;
import static com.myprescience.util.JSON.SERVER_ADDRESS;
import static com.myprescience.util.JSON.SONG_API;
import static com.myprescience.util.JSON.USER_API;
import static com.myprescience.util.JSON.USER_ID;
import static com.myprescience.util.JSON.USER_ID_WITH_FACEBOOK_ID;
import static com.myprescience.util.JSON.getLevel;
import static com.myprescience.util.JSON.getStringFromUrl;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {

    private static final String LIST_FRAGMENT_TAG = "list_fragment";

    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5, RATING = 0, BBT = 1;

    private int mode, song_count;
    private String genres;
    private int totalListSize;

    private Indicator mIndicator;

    private int selectCount = 0;
    private FrameLayout mFilterFragment;
    private ImageButton rightButton;
    private TextView textView;
    private ListView songListView;
    private SongListAdapter songListAdapter;
    private ProgressBar progressBar;
    private boolean mLockListView = false;

    private int mListCount;
    private int mListAddCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

//        Session session = Session.getActiveSession();
//        if(session != null){
//            if(session.isOpened()){
//                Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
//                    // callback after Graph API response with user object
//                    @Override
//                    public void onCompleted(GraphUser user, Response response) {
//                        new searchUserTask().execute(SERVER_ADDRESS+USER_API+USER_ID_WITH_FACEBOOK_ID+user.getId());
//                    }
//                });
//            }
//        }

        mListCount = 0;
        mListAddCount = 5;
        mIndicator = new Indicator(this);

        mFilterFragment = (FrameLayout) findViewById(R.id.filterFragment_Container);
        rightButton = (ImageButton) findViewById(R.id.nextButton);
        textView = (TextView) findViewById(R.id.top);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if(mode == BBT) {
            textView.setText("최소 " + MIN_SELECTED_SONG + "곡 이상 평가해주세요.");

            rightButton.setVisibility(ImageButton.INVISIBLE);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sSonglistActivity = SongListActivity.this;
                    Intent intent = new Intent(SongListActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        } else if(mode == RATING) {
            MIN_SELECTED_SONG = 300;
            rightButton.setImageResource(R.drawable.filter_menu);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int FragmentView = (mFilterFragment.getVisibility() == View.GONE)?
                            View.VISIBLE : View.GONE;
                    toggleList(FragmentView);
                }
            });
        }

        Intent intent = getIntent();
        mode = intent.getExtras().getInt("mode");
        selectSongsWithMode(mode, intent);

        songListAdapter = new SongListAdapter(SongListActivity.this, selectCount, progressBar, textView, rightButton, USER_ID);
        songListView = (ListView) findViewById(R.id.songListView);
        songListView.setAdapter(songListAdapter);

        // 스크롤 했을 때 마지막 셀이 보인다면 추가로 로딩
        songListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    selectSongsWithMode(mode, getIntent());
                    mLockListView = true;
                } else if(totalItemCount+mListAddCount > totalListSize && totalListSize != 0) {
                    mListAddCount =  totalListSize - (mListCount + 1);
                    Toast.makeText(getApplicationContext(), "노래를 전부 가져왔습니다." , Toast.LENGTH_LONG);
                    songListView.setOnScrollListener(null);
                }
            }
        });
    }

    private void toggleList(int Visibility) {
        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        } else {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.filterFragment_Container, SlidingListFragment
                                    .instantiate(this, SlidingListFragment.class.getName()),
                            LIST_FRAGMENT_TAG
                    ).addToBackStack(null).commit();
        }
        mFilterFragment.setVisibility(Visibility);
    }

//    class searchUserTask extends AsyncTask<String, String, Void> {
//
//        @Override
//        protected Void doInBackground(String... url) {
//            String userIdJSON = getStringFromUrl(url[0]);
//            JSONParser jsonParser = new JSONParser();
//            JSONArray users = null;
//            try {
//                users = (JSONArray) jsonParser.parse(userIdJSON);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            if(users != null) {
//                JSONObject user = (JSONObject) users.get(0);
//                userId = Integer.parseInt((String)user.get("user_id"));
//                new selectSongCountTask().execute(SERVER_ADDRESS+RATING_API+SELECT_SONG_COUNT+userId);
//            }
//            return null;
//        }
//    }

    class selectSongCountTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... url) {
            String userIdJSON = getStringFromUrl(url[0]);
            JSONParser jsonParser = new JSONParser();
            JSONArray users = null;
            try {
                users = (JSONArray) jsonParser.parse(userIdJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                song_count = Integer.parseInt((String)user.get("song_count"));
            }
            return song_count;
        }

        @Override
        protected void onPostExecute(Integer song_count) {
            textView.setText(getLevel(song_count));
        }
    }

    class getSimpleSongTask extends AsyncTask<String, String, String> {

        public getSimpleSongTask(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);
//            mLockListView = true;

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray songArray = (JSONArray) jsonParser.parse(songJSON);
                totalListSize = songArray.size();

                // mListCount는 추가 로드할 때 마다 10씩 증가
                for(int i = mListCount; i < mListCount+mListAddCount; i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(songArray.get(i).toString());

                    String id = (String)song.get("id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");
                    String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");

                    songListAdapter.addItem(id, spotifyAlbumID, title, artist, 0);

                    if(songListAdapter.getCount() > 4) {
                        songListAdapter.notifyDataSetChanged();

                        if ( mIndicator.isShowing())
                            mIndicator.hide();
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            mLockListView = false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
                if ( !mIndicator.isShowing())
                    mIndicator.show();
        }
    }

    private void selectSongsWithMode(int mode, Intent intent) {
        switch(mode) {
            case 0 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+RANDOM_SONGS);
                break;
            case 1 :
                ArrayList<String> selectGenre = intent.getExtras().getStringArrayList("selectGenre");
                genres = TextUtils.join(",", selectGenre);

                new getSimpleSongTask().execute(SERVER_ADDRESS+BILLBOARDTOP_API+BBT_WITH_GENRE+genres);
                break;
        }
    }

}
