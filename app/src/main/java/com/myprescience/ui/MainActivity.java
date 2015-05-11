package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.meetme.android.horizontallistview.HorizontalListView;
import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.album.AlbumListAdapter;
import com.myprescience.ui.song.MyPTopSongListActivity;
import com.myprescience.ui.song.SongActivity;
import com.myprescience.ui.song.SongListActivity;
import com.myprescience.util.BackPressCloseHandler;
import com.myprescience.util.ImageLoad;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.util.ArrayList;

import static com.myprescience.util.PixelUtil.getProperImage;
import static com.myprescience.util.Server.ALBUM_API;
import static com.myprescience.util.Server.MYP_HOT_SONGS;
import static com.myprescience.util.Server.RANDOM_MODE;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.SELECT_MAIN_LATEST_ALBUMS;
import static com.myprescience.util.Server.SELECT_SONG_COUNT;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private BackPressCloseHandler backPressCloseHandler;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private UserData userDTO;

    private Activity mActivity;
    private Indicator mIndicater;
    private ViewGroup mMyPTop1_FrameLayout, mMyPTop_LinearLayout1, mMyPTop_LinearLayout2, mMyPTop_LinearLayout3;
    private ArrayList<ViewGroup> mMyTopList;

    private LinearLayout mMypTop100Button, mMyPrescienceButton;
    private Button mMypTopMoreButton;

    private HorizontalListView mHorizontalListView;
    private AlbumListAdapter mHorizontalListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userDTO = new UserData(getApplicationContext());

        mIndicater = new Indicator(this);
        backPressCloseHandler = new BackPressCloseHandler(this);
        mActivity = this;

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mMyPTop1_FrameLayout = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout1);
        mMyPTop_LinearLayout1 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout1);
        mMyPTop_LinearLayout2 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout2);
        mMyPTop_LinearLayout3 = (ViewGroup) findViewById(R.id.MyPTop_LinearLayout3);

        mMyTopList = new ArrayList<ViewGroup>();
        mMyTopList.add(mMyPTop_LinearLayout1);
        mMyTopList.add(mMyPTop_LinearLayout2);
        mMyTopList.add(mMyPTop_LinearLayout3);

        mMypTop100Button = (LinearLayout) findViewById(R.id.mypTop100Button);
        mMypTop100Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPTopSongListActivity.class);
                startActivity(intent);
            }
        });

        mMypTopMoreButton = (Button) findViewById(R.id.mypTopMoreButton);
        mMypTopMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPTopSongListActivity.class);
                startActivity(intent);
            }
        });

        mMyPrescienceButton = (LinearLayout) findViewById(R.id.myPrescienceButton);
        mMyPrescienceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyPrescienceActivity.class);
                startActivity(intent);
            }
        });

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mHorizontalListView = (HorizontalListView) findViewById(R.id.HorizontalListView);
        mHorizontalListAdapter = new AlbumListAdapter(getApplicationContext(), userDTO.getId());
        mHorizontalListView.setAdapter(mHorizontalListAdapter);

    }

    public void initSetting() {
        new getMyPHotSongs().execute(SERVER_ADDRESS + SONG_API + MYP_HOT_SONGS);
        new getLatestAlbums().execute(SERVER_ADDRESS+ALBUM_API+SELECT_MAIN_LATEST_ALBUMS);
        new selectSongCountTask().execute(SERVER_ADDRESS+RATING_API+SELECT_SONG_COUNT+WITH_USER+userDTO.getId());
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                Intent section2 = new Intent(this, MyPrescienceActivity.class);
                startActivity(section2);
                break;
            case 3:
//                mTitle = getString(R.string.title_section2);
                Intent section3 = new Intent(this, SongListActivity.class);
                section3.putExtra("mode", RANDOM_MODE);
                startActivity(section3);
                break;
            case 4:
//                mTitle = getString(R.string.title_section3);
                Intent section4 = new Intent(this, MyPageActivity.class);
                startActivity(section4);
                break;
            case 5:
                Intent section5 = new Intent(this, SelectGenreActivity.class);
                startActivity(section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
        actionBar.setTitle("");
        actionBar.show();

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true);
//        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
//        actionBar.setCustomView(R.layout.actionbar_title);
//        mTitleTextView = (TextView) findViewById(R.id.customActionbarTitle);
//        mTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "NEOTERIC.ttf"));
//        mTitleTextView.setText(mTitle);
//        actionBar.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.search_menu, menu);
            restoreActionBar();
            return true;
        } else {
            getSupportActionBar().hide();
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Log.e("Search" , "여기서 검색 관련 트리거를 넣으면 될 듯!");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void setMypTop1View(ViewGroup framelayout, int rank, float avg, int count, String title, String artist) {

        ViewGroup linearLayout = (ViewGroup) framelayout.getChildAt(2);
        ViewGroup inLinearLayout = (ViewGroup) linearLayout.getChildAt(0);
        RatingBar avgRatingBar = (RatingBar) inLinearLayout.getChildAt(0);
        TextView avgTextView = (TextView) inLinearLayout.getChildAt(1);
        TextView countTextView = (TextView) linearLayout.getChildAt(1);
        TextView titleTextView = (TextView) linearLayout.getChildAt(2);
        TextView artistTextView = (TextView) linearLayout.getChildAt(3);

        LayerDrawable stars = (LayerDrawable) avgRatingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

        int avg_rating = Math.round(avg);

        avgRatingBar.setProgress(avg_rating);
        avgTextView.setText(String.format("(%.1f)", avg/2));
        countTextView.setText(count + "명이 평가했습니다.");
        titleTextView.setText(title);
        artistTextView.setText(artist);
    }

    public ImageView getMypTop1ImageView(ViewGroup framelayout) {
        return (ImageView) framelayout.getChildAt(0);
    }

    public void setMypTopListView(ViewGroup linearLayout, float avg, int count, String title, String artist) {

        ViewGroup inLinearLayout = (ViewGroup) linearLayout.getChildAt(1);
        TextView titleTextView = (TextView) inLinearLayout.getChildAt(0);
        TextView artistTextView = (TextView) inLinearLayout.getChildAt(1);

        ViewGroup ininLinearLayout = (ViewGroup) inLinearLayout.getChildAt(2);
        TextView avgRatingTextView = (TextView) ininLinearLayout.getChildAt(1);

        TextView countTextView = (TextView) inLinearLayout.getChildAt(3);

        titleTextView.setText(title);
        artistTextView.setText(artist);

        avgRatingTextView.setText(String.format("%.1f", avg/2.0));
        countTextView.setText(count + "명이 평가했습니다.");
    }

    public ImageView getMypTopListImageView(ViewGroup linearLayout) {
        return (ImageView) linearLayout.getChildAt(0);
    }

    class getMyPHotSongs extends AsyncTask<String, String, String> {

        public getMyPHotSongs(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String myphotJSON) {
            super.onPostExecute(myphotJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray hots = null;
            try {
                hots = (JSONArray) jsonParser.parse(myphotJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < 4; i++) {

                JSONObject hot = (JSONObject) hots.get(i);
                final String id = (String) hot.get("id");
                String artist_spotify_id = (String) hot.get("artist_spotify_id");
                String title = (String) hot.get("title");
                String artist = (String) hot.get("artist");
                float avg = Float.parseFloat((String) hot.get("avg"));
                int rating_count = Integer.parseInt((String) hot.get("rating_count"));

                if(i == 0) {
                    setMypTop1View(mMyPTop1_FrameLayout, 1, avg, rating_count, title, artist);

                    ImageView albumArt = getMypTop1ImageView(mMyPTop1_FrameLayout);
                    albumArt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                            intent.putExtra("song_id", id);
                            startActivity(intent);
                        }
                    });

                    if (!artist_spotify_id.equals(""))
                        new getSpotifyArtistImage(mMyPTop1_FrameLayout).execute(SPOTIFY_API + "artists/" + artist_spotify_id);
                } else {
                    setMypTopListView(mMyTopList.get(i - 1), avg, rating_count, title, artist);

                    ImageView albumArt = getMypTopListImageView(mMyTopList.get(i - 1));
                    albumArt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                            intent.putExtra("song_id", id);
                            startActivity(intent);
                        }
                    });
                    if (!artist_spotify_id.equals(""))
                    new getSpotifyArtistImage(mMyTopList.get(i-1)).execute(SPOTIFY_API + "artists/" + artist_spotify_id);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            if(!mIndicater.isShowing())
                mIndicater.show();
        }
    }

    public class getSpotifyArtistImage extends AsyncTask<String, String, String> {

        private ViewGroup myPHot_FrameLayout;

        public getSpotifyArtistImage(ViewGroup _myPHot_FrameLayout) {
            this.myPHot_FrameLayout = _myPHot_FrameLayout;
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String artistJSON) {
            super.onPostExecute(artistJSON);

            JSONParser jsonParser = new JSONParser();
            JSONObject artist = null;
            try {
                artist = (JSONObject) jsonParser.parse(artistJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            JSONArray images = (JSONArray) artist.get("images");
            if(images.size() != 0) {
                JSONObject image = getProperImage(images, getMypTop1ImageView(myPHot_FrameLayout).getWidth());
                String url = (String) image.get("url");
                new ImageLoad(mActivity, mIndicater, url, getMypTop1ImageView(myPHot_FrameLayout)).execute();
            } else {
                getMypTop1ImageView(myPHot_FrameLayout).setImageResource(R.drawable.not_exist);
            }
        }
    }

    class getLatestAlbums extends AsyncTask<String, String, String> {

        public getLatestAlbums(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String albumJSON) {
            super.onPostExecute(albumJSON);

            JSONParser jsonParser = new JSONParser();
            JSONArray albums = null;
            try {
                albums = (JSONArray) jsonParser.parse(albumJSON);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < albums.size(); i++) {
                JSONObject album = (JSONObject) albums.get(i);
                final String id = (String) album.get("id");
                String name = (String) album.get("name");
                String artist = (String) album.get("artist");
                String release_date = (String) album.get("release_date");
                String image_300 = (String) album.get("image_300");

                mHorizontalListAdapter.addItem(id, name, artist, release_date, image_300);
            }
            mHorizontalListAdapter.notifyDataSetChanged();

        }

        @Override
        protected void onPreExecute() {
            if(!mIndicater.isShowing())
                mIndicater.show();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();

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

            int songCount = 0;
            if(users != null) {
                JSONObject user = (JSONObject) users.get(0);
                songCount = Integer.parseInt((String)user.get("song_count"));
            }
            return songCount;
        }

        @Override
        protected void onPostExecute(Integer song_count) {
            Log.e("song_count", song_count+"");
            userDTO.setRatingSongCount(song_count);
        }
    }

}
