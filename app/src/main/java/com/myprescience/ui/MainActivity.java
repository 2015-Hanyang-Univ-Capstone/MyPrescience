package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
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
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.myprescience.R;
import com.myprescience.util.ImageLoad;
import com.myprescience.util.Indicator;

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
import static com.myprescience.util.Server.ECHONEST_GENRE_SEARCH;
import static com.myprescience.util.Server.GENRES_API;
import static com.myprescience.util.Server.INSERT_GENRE_DETAIL;
import static com.myprescience.util.Server.MYP_HOT_SONGS;
import static com.myprescience.util.Server.RANDOM_MODE;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.SONG_API;
import static com.myprescience.util.Server.SPOTIFY_API;
import static com.myprescience.util.Server.getStringFromUrl;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private Activity mActivity;
    private Indicator mIndicater;
    private ViewGroup mMyPHot_FrameLayout1, mMyPHot_FrameLayout2, mMyPHot_FrameLayout3, mMyPHot_FrameLayout4, mMyPHot_FrameLayout5,
            mMyPHot_FrameLayout6, mMyPHot_FrameLayout7;
    private TextView mTitleTextView;
    private ArrayList<ViewGroup> mMyPHotList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIndicater = new Indicator(this);
        mActivity = this;

//        RecommendActivity.sRecommendActivity.finish();
//        SongListActivity.sSonglistActivity.finish();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        mMyPHot_FrameLayout1 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout1);
        mMyPHot_FrameLayout2 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout2);
        mMyPHot_FrameLayout3 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout3);
        mMyPHot_FrameLayout4 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout4);
        mMyPHot_FrameLayout5 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout5);
        mMyPHot_FrameLayout6 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout6);
        mMyPHot_FrameLayout7 = (ViewGroup) findViewById(R.id.MyPHot_FrameLayout7);

        mMyPHotList = new ArrayList<ViewGroup>();
        mMyPHotList.add(mMyPHot_FrameLayout1);
        mMyPHotList.add(mMyPHot_FrameLayout2);
        mMyPHotList.add(mMyPHot_FrameLayout3);
        mMyPHotList.add(mMyPHot_FrameLayout4);
        mMyPHotList.add(mMyPHot_FrameLayout5);
        mMyPHotList.add(mMyPHot_FrameLayout6);
        mMyPHotList.add(mMyPHot_FrameLayout7);

        new getMyPHotSongs().execute(SERVER_ADDRESS+SONG_API+MYP_HOT_SONGS);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
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

    public void setMypHotView(ViewGroup framelayout, int rank, float avg, int count, String title, String artist) {

        TextView rankTextView = (TextView) framelayout.getChildAt(1);
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

        rankTextView.setText("Top." + rank);
        avgRatingBar.setProgress(avg_rating);
        avgTextView.setText(String.format("(%.1f)", avg/2));
        countTextView.setText(count + "명이 평가했습니다.");
        titleTextView.setText(title);
        artistTextView.setText(artist);
    }

    public ImageView getMypHotImageView(ViewGroup framelayout) {
        return (ImageView) framelayout.getChildAt(0);
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

            for(int i = 0; i < hots.size(); i++) {
                JSONObject hot = (JSONObject) hots.get(i);
                final String id = (String) hot.get("id");
                String artist_spotify_id = (String) hot.get("artist_spotify_id");
                String title = (String) hot.get("title");
                String artist = (String) hot.get("artist");
                float avg = Float.parseFloat((String) hot.get("avg"));
                int rating_count = Integer.parseInt((String) hot.get("rating_count"));

                setMypHotView(mMyPHotList.get(i), i+1, avg, rating_count, title, artist);

                ImageView albumArt = getMypHotImageView(mMyPHotList.get(i));
                albumArt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), SongActivity.class);
                        intent.putExtra("song_id", id);
                        startActivity(intent);
                    }
                });

                if(!artist_spotify_id.equals(""))
                    new getSpotifyArtistImage(i).execute(SPOTIFY_API+"artists/"+artist_spotify_id);
            }
        }

        @Override
        protected void onPreExecute() {
            if(!mIndicater.isShowing())
                mIndicater.show();
        }
    }

    public class getSpotifyArtistImage extends AsyncTask<String, String, String> {

        private int index;

        public getSpotifyArtistImage(int index) {
            this.index = index;
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
                JSONObject image = getProperImage(images, getMypHotImageView(mMyPHotList.get(index)).getWidth());
                String url = (String) image.get("url");
                new ImageLoad(mActivity, mIndicater, url, getMypHotImageView(mMyPHotList.get(index))).execute();
            } else {
                getMypHotImageView(mMyPHotList.get(index)).setImageResource(R.drawable.not_exist);
            }
        }
    }



}
