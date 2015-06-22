package com.myprescience.ui.song;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import com.myprescience.search.SearchListActivity;
import com.myprescience.ui.main.MainActivity;
import com.myprescience.util.Indicator;
import com.myprescience.util.RoundImage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

import static com.myprescience.util.Server.BILLBOARD_API;
import static com.myprescience.util.Server.FIRST_MODE;
import static com.myprescience.util.Server.GENRE_TOP;
import static com.myprescience.util.Server.HOT100;
import static com.myprescience.util.Server.MODE;
import static com.myprescience.util.Server.MYP_RANK_SONGS;
import static com.myprescience.util.Server.RANDOM_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SONG_WTIH_CLAUSE;
import static com.myprescience.util.Server.SYNC_MODE;
import static com.myprescience.util.Server.TODAY_SONG_MODE;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getLevelDescribe;
import static com.myprescience.util.Server.getStringFromUrl;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends ActionBarActivity implements SongFilterFragment.OnFilterSelectedListener {

    private UserData userDTO;

    private static final String LIST_FRAGMENT_TAG = "list_fragment";

    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 15;

    private String genres;
    private Intent modeIntent;

    private Indicator mIndicator;

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

        setContentView(R.layout.activity_song_list);
        userDTO = new UserData(getApplicationContext());
        setActionBar(R.string.title_activity_rating);


        initSongList();

        mIndicator = new Indicator(this);

        mFilterFragment = (FrameLayout) findViewById(R.id.filterFragment_Container);
        rightButton = (ImageButton) findViewById(R.id.nextButton);
        textView = (TextView) findViewById(R.id.top);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        modeIntent = getIntent();
        MODE = modeIntent.getExtras().getInt("mode");

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
        } else if(MODE == TODAY_SONG_MODE || MODE == SYNC_MODE) {
            MIN_SELECTED_SONG = 500;

            if(userDTO.getRatingSongCount() > 400)
                textView.setText(getString(getLevelDescribe(400)));
            else
                textView.setText(getString(getLevelDescribe(userDTO.getRatingSongCount())));
            progressBar.setProgress((int)(Math.min(1, userDTO.getRatingSongCount()/(double) MIN_SELECTED_SONG)*100));
            progressBar.invalidate();

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
        }

        songListAdapter = new SongListAdapter(SongListActivity.this, userDTO.getRatingSongCount(), progressBar, textView, rightButton, userDTO.getId());
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
                if ((totalItemCount + mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount)
                        && (mLockListView == false) && (totalItemCount > 0)) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    selectSongsWithMode(MODE, getIntent());
                    mLockListView = true;
                } else if (totalItemCount + mListAddCount > totalListSize && totalListSize != 0) {
                    mListCount += mListAddCount;
                    mListAddCount = totalListSize - mListCount;
                    selectSongsWithMode(MODE, getIntent());
                    Toast.makeText(getApplicationContext(), "노래를 전부 가져왔습니다.", Toast.LENGTH_LONG);
                    songListView.setOnScrollListener(null);
                }
            }
        });

        selectSongsWithMode(MODE, modeIntent);
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
        songListAdapter = new SongListAdapter(SongListActivity.this, userDTO.getRatingSongCount(), progressBar, textView, rightButton, userDTO.getId());
        songListView.setAdapter(songListAdapter);
        songListAdapter.notifyDataSetChanged();
        selectSongsWithMode(mode, modeIntent);
        int FragmentView = (mFilterFragment.getVisibility() == View.GONE)?
                View.VISIBLE : View.GONE;
        toggleList(FragmentView);
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
                    String spotifyArtistID = (String) song.get("artist_spotify_id");
                    String title = (String)song.get("title");
                    String artist = (String)song.get("artist");
                    String image_64 = (String)song.get("image_64");
                    String ratingStr = (String)song.get("rating");
                    int rating = 0;
                    if(ratingStr != null) {
                        rating = Integer.parseInt(ratingStr);
                    }
                    songListAdapter.addItem(id, spotifyArtistID, image_64, title, artist, rating);

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

        String korFilter = "domestic";
        String popFilter = "oversea";
        String bbhot100Filter = "country%20=%20%27bbhot100%27";

        String genrePopFilter = "pop";
        String genreHiphopFilter = "hiphop";
        String genreRnBFilter = "rnb";
        String genreRockFilter = "rock";
        String genreCountryFilter = "country";
        String genreElectronicFilter = "electro";
        String genreJazzFilter = "jazz";
        String genreDanceFilter = "dance";

        String degree = "0.75";
        String valenceFilter = "valence";
        String loudnessFilter = "loudness";
        String dancabilityFilter = "danceability";
        String energyFilter = "energy";
        String livenessFilter = "liveness";
        String speechinessFilter = "speechiness";
        String acousticFilter = "acousticness";
        String instrumentalFilter = "instrumentalness";

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
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        korFilter+WITH_USER+userDTO.getId());
                break;
            case 3 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        popFilter+WITH_USER+userDTO.getId());
                break;
            case 4 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+BILLBOARD_API+HOT100+WITH_USER+userDTO.getId());
                break;


            case 41 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genrePopFilter+WITH_USER+userDTO.getId());
                break;
            case 42 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreHiphopFilter+WITH_USER+userDTO.getId());
                break;
            case 43 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreRnBFilter+WITH_USER+userDTO.getId());
                break;
            case 44 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreRockFilter+WITH_USER+userDTO.getId());
                break;
            case 45 :
                new getSimpleSongTask().execute(SERVER_ADDRESS + SONG_API + SONG_WTIH_CLAUSE +
                        genreCountryFilter + WITH_USER + userDTO.getId());
                break;
            case 46 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreElectronicFilter+WITH_USER+userDTO.getId());
                break;
            case 47 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreJazzFilter+WITH_USER+userDTO.getId());
                break;
            case 48 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        genreDanceFilter+WITH_USER+userDTO.getId());
                break;


            case 101 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        valenceFilter+WITH_USER+userDTO.getId());
                break;
            case 102 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        loudnessFilter+WITH_USER+userDTO.getId());
                break;
            case 103 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        dancabilityFilter+WITH_USER+userDTO.getId());
                break;
            case 104 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        energyFilter+WITH_USER+userDTO.getId());
                break;
            case 105 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        livenessFilter+WITH_USER+userDTO.getId());
                break;
            case 106 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        speechinessFilter+WITH_USER+userDTO.getId());
                break;
            case 107 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        acousticFilter+WITH_USER+userDTO.getId());
                break;
            case 108 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+SONG_WTIH_CLAUSE+
                        instrumentalFilter+WITH_USER+userDTO.getId());
                break;
            case 109 :
                new getSimpleSongTask().execute(SERVER_ADDRESS+SONG_API+MYP_RANK_SONGS+WITH_USER+userDTO.getId());
                break;

            case 200 :
                String syncJSON = intent.getExtras().getString("syncJSON");
                JSONParser jsonParser = new JSONParser();
                JSONArray songs = null;
                try {
                    songs = (JSONArray) jsonParser.parse(syncJSON);
                    for(int i = 0; i < songs.size(); i++) {
                        JSONObject song = (JSONObject) jsonParser.parse(songs.get(i).toString());

                        String id = (String)song.get("id");
                        String spotifyArtistID = (String) song.get("artist_spotify_id");
                        String title = (String)song.get("title");
                        String artist = (String)song.get("artist");
                        String spotifyAlbumID = "albums/"+(String)song.get("album_spotify_id");
                        String ratingStr = (String)song.get("rating");
                        int rating = 0;
                        if(ratingStr != null) {
                            rating = Integer.parseInt(ratingStr);
                        }
                        songListAdapter.addItem(id, spotifyArtistID, spotifyAlbumID, title, artist, rating);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
            case R.id.action_search:
                Intent intent = new Intent(this, SearchListActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

}
