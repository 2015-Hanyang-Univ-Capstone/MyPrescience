package com.myprescience.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;
import com.myprescience.util.RoundImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import static com.myprescience.util.Server.ACOUSTIC_SONGS;
import static com.myprescience.util.Server.BILLBOARD_API;
import static com.myprescience.util.Server.DANCEABILITY_SONGS;
import static com.myprescience.util.Server.ENERGY_SONGS;
import static com.myprescience.util.Server.FIRST_MODE;
import static com.myprescience.util.Server.GENRE_TOP;
import static com.myprescience.util.Server.HOT100;
import static com.myprescience.util.Server.INSTRUMENTALNESS_SONGS;
import static com.myprescience.util.Server.KOR_SONGS;
import static com.myprescience.util.Server.LIVENESS_SONGS;
import static com.myprescience.util.Server.LOUDNESS_SONGS;
import static com.myprescience.util.Server.MODE;
import static com.myprescience.util.Server.MYP_RANK_SONGS;
import static com.myprescience.util.Server.RANDOM_MODE;
import static com.myprescience.util.Server.RANDOM_SONGS;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_SONG_COUNT;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SPEECHINCESS_SONGS;
import static com.myprescience.util.Server.VALENCE_SONGS;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getLevel;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity implements SongFilterFragment.OnFilterSelectedListener {

    private UserData userDTO = new UserData();

    private static final String LIST_FRAGMENT_TAG = "list_fragment";

    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;

    private int song_count;
    private String genres;
    private Intent modeIntent;

    private Indicator mIndicator;

    private int selectCount = 0;
    private FrameLayout mFilterFragment;
    private ImageButton rightButton;
    private TextView textView;
    private ListView songListView;
    private SongListAdapter songListAdapter;
    private ProgressBar progressBar;
    private boolean mLockListView = false;

    private int totalListSize;
    private int mListCount;
    private int mListAddCount;
    private JSONArray mSongArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getActionBar();

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar_title);

        TextView TitleTextView = (TextView) findViewById(R.id.customActionbarTitle);
        TitleTextView.setText(R.string.title_section3);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

//        actionBar.setHomeButtonEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_song_list);

        initSongList();

        mIndicator = new Indicator(this);

        mFilterFragment = (FrameLayout) findViewById(R.id.filterFragment_Container);
        rightButton = (ImageButton) findViewById(R.id.nextButton);
        textView = (TextView) findViewById(R.id.top);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        modeIntent = getIntent();
        MODE = modeIntent.getExtras().getInt("mode");
        selectSongsWithMode(MODE, modeIntent);

        if(MODE == FIRST_MODE) {
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
        } else if(MODE == RANDOM_MODE) {
            MIN_SELECTED_SONG = 300;
            Bitmap image_bit = BitmapFactory.decodeResource(getResources(),
                    R.drawable.icon_filter);
            RoundImage image = new RoundImage(image_bit);
            rightButton.setImageDrawable(image);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int FragmentView = (mFilterFragment.getVisibility() == View.GONE)?
                            View.VISIBLE : View.GONE;
                    toggleList(FragmentView);
                }
            });

            new selectSongCountTask().execute(SERVER_ADDRESS+RATING_API+SELECT_SONG_COUNT+WITH_USER+userDTO.getId());
        }

        songListAdapter = new SongListAdapter(SongListActivity.this, selectCount, progressBar, textView, rightButton, userDTO.getId());
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
                    selectSongsWithMode(MODE, getIntent());
                    mLockListView = true;
                } else if(totalItemCount+mListAddCount > totalListSize && totalListSize != 0) {
                    mListCount += mListAddCount;
                    mListAddCount =  totalListSize - mListCount;
                    selectSongsWithMode(MODE, getIntent());
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
            Log.e("TEST", "UP");
        } else {
            Log.e("TEST", "DOWN");
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up,
                            R.anim.slide_down,
                            R.anim.slide_up,
                            R.anim.slide_down)
                    .add(R.id.filterFragment_Container, SongFilterFragment
                                    .instantiate(this, SongFilterFragment.class.getName()),
                            LIST_FRAGMENT_TAG
                    ).addToBackStack(null).commit();
        }
        mFilterFragment.setVisibility(Visibility);
    }

    public void initSongList() {
        mListCount = 0;
        mListAddCount = 5;
        mSongArray = null;
    }

    @Override
    public void onFilterSelected(int mode) {
        initSongList();
        MODE = mode;
        songListAdapter = new SongListAdapter(SongListActivity.this, selectCount, progressBar, textView, rightButton, userDTO.getId());
        songListView.setAdapter(songListAdapter);
        songListAdapter.notifyDataSetChanged();
        selectSongsWithMode(mode, modeIntent);
        int FragmentView = (mFilterFragment.getVisibility() == View.GONE)?
                View.VISIBLE : View.GONE;
        toggleList(FragmentView);
    }

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
            songListAdapter.setProgress(song_count);
        }
    }

    class getSimpleSongTask extends AsyncTask<String, String, String> {

        public getSimpleSongTask(){
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
//            mLockListView = true;

            try {
                JSONParser jsonParser = new JSONParser();
                if(songJSON != null) {
                    mSongArray = (JSONArray) jsonParser.parse(songJSON);
                    totalListSize = mSongArray.size();
                }
                // mListCount는 추가 로드할 때 마다 10씩 증가
                for(int i = mListCount; i < mListCount+mListAddCount; i ++) {

                    JSONObject song = (JSONObject) jsonParser.parse(mSongArray.get(i).toString());

                    String id = (String)song.get("id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");
                    String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                    String ratingStr = (String)song.get("rating");
                    int rating = 0;
                    if(ratingStr != null) {
                        rating = Integer.parseInt(ratingStr);
                    }
                    songListAdapter.addItem(id, spotifyAlbumID, title, artist, rating);

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

    public void selectSongsWithMode(int mode, Intent intent) {

        Log.e("mode", mode+"");

        switch(mode) {
            case 0 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+RANDOM_SONGS+WITH_USER+userDTO.getId());
                break;
            case 1 :
                ArrayList<String> selectGenre = intent.getExtras().getStringArrayList("selectGenre");
                genres = TextUtils.join(",", selectGenre);

                new getSimpleSongTask().execute(SERVER_ADDRESS+BILLBOARD_API+GENRE_TOP+genres+WITH_USER+userDTO.getId());
                break;
            case 2 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+KOR_SONGS+WITH_USER+userDTO.getId());
                break;
            case 4 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+BILLBOARD_API+HOT100+WITH_USER+userDTO.getId());
                break;
            case 5 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+VALENCE_SONGS+WITH_USER+userDTO.getId());
                break;
            case 6 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+LOUDNESS_SONGS+WITH_USER+userDTO.getId());
                break;
            case 7 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+DANCEABILITY_SONGS+WITH_USER+userDTO.getId());
                break;
            case 8 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+ENERGY_SONGS+WITH_USER+userDTO.getId());
                break;
            case 9 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+LIVENESS_SONGS+WITH_USER+userDTO.getId());
                break;
            case 10 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SPEECHINCESS_SONGS+WITH_USER+userDTO.getId());
                break;
            case 11 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+ACOUSTIC_SONGS+WITH_USER+userDTO.getId());
                break;
            case 12 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+INSTRUMENTALNESS_SONGS+WITH_USER+userDTO.getId());
                break;
            case 13 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+MYP_RANK_SONGS+WITH_USER+userDTO.getId());
                break;
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
