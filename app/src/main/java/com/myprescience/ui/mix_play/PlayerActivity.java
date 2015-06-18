package com.myprescience.ui.mix_play;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.material.ProgressBarCircular;
import com.myprescience.util.BackPressCloseHandler;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.myprescience.util.Server.YOUTUBE_API;
import static com.myprescience.util.Server.YOUTUBE_API_KEY;
import static com.myprescience.util.Server.YOUTUBE_DEVELOPMENT_KEY;
import static com.myprescience.util.Server.YOUTUBE_RESULT_ONE;
import static com.myprescience.util.Server.getStringFromUrl;

import static com.myprescience.ui.mix_play.SongFragment.SPOTIFY_ARTIST_ID;
import static com.myprescience.ui.mix_play.SongFragment.SPOTIFY_ALBUM_ID;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class PlayerActivity extends YouTubeBaseActivity {

    private static final String LIST_FRAGMENT_TAG = "song_fragment";

    private UserData userDTO;
    private BackPressCloseHandler backPressCloseHandler;
    private YouTubePlayer mPlayer;
    private YouTubePlayerView mYouTubeView;

    private ArrayList<String> youtubes_id, youtubes_title, song_id;
    private String[] mPlaylist_id;
    private int mCurrent_Video;

    private FrameLayout mFilterFragment;
    private LinearLayout mPlayerBarLayout, mPlayerLoadingProgressBar;
    private ImageView mPlayerSongListButtonView, mPlayerPrevButtonView, mPlayerPlayButtonView, mPlayerNextButtonView;
    private TextView mPlayerTitleTextView;
    private CardView mPlayerPlayListView;
    private Button mPlayListArtistButton, mPlayListAlbumButton, mPlayListSongButton, mPlayListCloseButton;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_player);
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);
        backPressCloseHandler = new BackPressCloseHandler(this);

        mFilterFragment = (FrameLayout) findViewById(R.id.filterFragment_Container);

        mPlayerBarLayout = (LinearLayout) findViewById(R.id.playerBarLayout);
        mPlayerLoadingProgressBar = (LinearLayout) findViewById(R.id.playerLoadingProgressBar);
        ProgressBarCircular progressBarCircular = (ProgressBarCircular) findViewById(R.id.loadingProgressBar);
        progressBarCircular.setBackgroundColor(getResources().getColor(R.color.color_base_theme));

        mPlayerPrevButtonView = (ImageView) findViewById(R.id.playerPrevButton);
        mPlayerPrevButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayer.previous();
            }
        });

        mPlayerPlayButtonView = (ImageView) findViewById(R.id.playerPlayButton);
        mPlayerPlayButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer.isPlaying())
                    mPlayer.pause();
                else
                    mPlayer.play();
            }
        });

        mPlayerNextButtonView = (ImageView) findViewById(R.id.playerNextButton);
        mPlayerNextButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mPlayer.hasNext()) {
                    mCurrent_Video++;
                    mPlayer.loadVideos(youtubes_id, mCurrent_Video, youtubes_id.size()-1);
                } else {
                    mPlayer.next();
                }

            }
        });

        mPlayerTitleTextView = (TextView) findViewById(R.id.playerTitleTextView);

        mPlayerPlayListView = (CardView) findViewById(R.id.playerPlayListView);
        mPlayerSongListButtonView = (ImageView) findViewById(R.id.playerSongListButtonView);
        mPlayerSongListButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int dpValue = 400; // margin in dips
                float dpPoint = getApplicationContext().getResources().getDisplayMetrics().density;
                int marginRight = (int)(dpValue * dpPoint); // margin in pixels
                int marginBottom = mPlayerBarLayout.getHeight();

                RelativeLayout.LayoutParams youtubeMargin = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                youtubeMargin.setMargins(0, 0, marginRight, marginBottom);
                mYouTubeView.setLayoutParams(youtubeMargin);

//                viewFadeIn(mPlayerPlayListView);

                int FragmentView = (mPlayerPlayListView.getVisibility() == View.GONE)?
                        View.VISIBLE : View.GONE;
                mPlayerPlayListView.setVisibility(FragmentView);
            }
        });

        mPlayListArtistButton = (Button) findViewById(R.id.playListArtistButton);
        mPlayListArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SPOTIFY_ARTIST_ID != null)
                    getArtistFragment();
                else
                    Toast.makeText(PlayerActivity.this, "음악 정보를 받아오는 중 입니다. 조금 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        mPlayListAlbumButton = (Button) findViewById(R.id.playListAlbumButton);
        mPlayListAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SPOTIFY_ALBUM_ID != null)
                    getAlbumFragment();
                else
                    Toast.makeText(PlayerActivity.this, "음악 정보를 받아오는 중 입니다. 조금 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        mPlayListSongButton = (Button) findViewById(R.id.playListSongButton);
        mPlayListSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSongFragment();
            }
        });

        mPlayListCloseButton = (Button) findViewById(R.id.playListCloseButton);
        mPlayListCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewFadeOut(mPlayerPlayListView);

                int marginBottom = mPlayerBarLayout.getHeight();

                RelativeLayout.LayoutParams youtubeMargin = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                youtubeMargin.setMargins(0, 0, 0, marginBottom);
                mYouTubeView.setLayoutParams(youtubeMargin);
            }
        });

        youtubes_id = new ArrayList<String>(200);
        youtubes_title = new ArrayList<String>(200);
        song_id = new ArrayList<String>(200);

        mYouTubeView = (YouTubePlayerView) findViewById(R.id.playerYouTubeView);

        String[] playlist = getIntent().getExtras().getStringArray("playlist");
        mPlaylist_id = getIntent().getExtras().getStringArray("playlist_id");

        for(int i = 0; i < playlist.length; i++) {
            boolean start = false;
            if(i == 0)
                start = true;
            try {
                if(playlist[i] != null && !this.isFinishing()) {
                    new setYouTubeTask(mYouTubeView, start, i).execute(YOUTUBE_API + URLEncoder.encode(playlist[i], "utf-8")
                            + YOUTUBE_RESULT_ONE + YOUTUBE_API_KEY);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);
        return true;
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_playlist:
                // NavUtils.navigateUpFromSameTask(this);
                Log.e("PlayList", "PlayList가 보여지도록.");
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

    private void viewFadeIn(View layout) {
        Animation animation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.abc_fade_in);
        layout.startAnimation(animation);
        layout.setVisibility(View.VISIBLE);
    }

    private void viewFadeOut(View layout) {
        Animation animation = AnimationUtils.loadAnimation(PlayerActivity.this, R.anim.abc_fade_out);
        layout.startAnimation(animation);
        layout.setVisibility(View.GONE);
    }

    class setYouTubeTask extends AsyncTask<String, String, String> implements YouTubePlayer.OnInitializedListener {

        private YouTubePlayerView mYouTubePlayerView;
        private int mIndex;
        private boolean mVideoStart;

        public setYouTubeTask(YouTubePlayerView _YouTubePlayerView, boolean _videoStart, int _index){
            this.mYouTubePlayerView = _YouTubePlayerView;
            this.mVideoStart = _videoStart;
            this.mIndex = _index;
        }

        @Override
        protected void onCancelled() {
            // TODO 작업이 취소된후에 호출된다.
            super.onCancelled();
        }

        @Override
        protected String doInBackground(String... url) {
            if (this.isCancelled()) {
                // 비동기작업을 cancel해도 자동으로 취소해주지 않으므로,
                // 작업중에 이런식으로 취소 체크를 해야 한다.
                return null;
            }
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String youtubeJSON) {
            super.onPostExecute(youtubeJSON);

            if(youtubeJSON == null)
                return ;

            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject youtube = (JSONObject) jsonParser.parse(youtubeJSON);
                JSONArray items = (JSONArray) youtube.get("items");
                if(items.size()!= 0) {
                    for(int i = 0; i < items.size(); i ++) {
                        JSONObject id = (JSONObject) ((JSONObject) items.get(i)).get("id");
                        String video_id = (String) id.get("videoId");
                        youtubes_id.add(video_id);

                        JSONObject snippet = (JSONObject) ((JSONObject) items.get(i)).get("snippet");
                        String video_title = (String) snippet.get("title");
                        youtubes_title.add(video_title);
                    }
                    if(mPlaylist_id[mIndex] != null)
                        song_id.add(mPlaylist_id[mIndex]);
                }
                if(mVideoStart) {
                    mPlayerLoadingProgressBar.setVisibility(View.GONE);
                    mYouTubePlayerView.initialize(YOUTUBE_DEVELOPMENT_KEY, this);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                            YouTubeInitializationResult errorReason) {
            if (errorReason.isUserRecoverableError()) {
                errorReason.getErrorDialog(PlayerActivity.this, 1).show();
            } else {
                String errorMessage = String.format(
                        getString(R.string.YouTubeError), errorReason.toString());
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }
        }



        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                            final YouTubePlayer player, boolean wasRestored) {
            mPlayer = player;
            mCurrent_Video = 0;


            if (!wasRestored && youtubes_id.size() != 0) {
                player.loadVideos(youtubes_id);
                Log.e("youtubes_id Count", youtubes_id.size() + "");
                mPlayerTitleTextView.setText(youtubes_title.get(mCurrent_Video));
//                for(int i = 0; i < youtubes_title.size(); i++) {
//                    mPlaylist.get(i).setText( (i+1) + ". " + youtubes_title.get(i) );
//                    mPlaylist.get(i).setTag(youtubes_id.get(i));
//                    final int finalI = i;
//                    mPlaylist.get(i).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            for(int j = 0; j < youtubes_title.size(); j ++)
//                                mPlaylist.get(j).setTextColor(getResources().getColor(R.color.darker_gray));
//                            mCurrent_Video = finalI;
//                            player.loadVideo((String) v.getTag());
//                            TextView textView = (TextView) v;
//                            textView.setTextColor(getResources().getColor(R.color.WhiteSmoke));
//                        }
//                    });
//                }
            }



//            mPlayAllButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    player.cueVideos(youtubes_id);
//                    for(int j = 0; j < youtubes_title.size(); j ++)
//                        mPlaylist.get(j).setTextColor(getResources().getColor(R.color.darker_gray));
//                    mPlaylist.get(0).setTextColor(getResources().getColor(R.color.WhiteSmoke));
//                }
//            });

            player.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
                @Override
                public void onPlaying() {
                    mPlayerPlayButtonView.setImageDrawable(getResources().getDrawable(R.drawable.icon_player_pause));
                }

                @Override
                public void onPaused() {
                    mPlayerPlayButtonView.setImageDrawable(getResources().getDrawable(R.drawable.icon_player_play));
                }

                @Override
                public void onStopped() {
                    mPlayerPlayButtonView.setImageDrawable(getResources().getDrawable(R.drawable.icon_player_play));
                }

                @Override
                public void onBuffering(boolean b) {

                }

                @Override
                public void onSeekTo(int i) {

                }
            });

            player.setPlaylistEventListener(new YouTubePlayer.PlaylistEventListener() {

                @Override
                public void onPrevious() {
//                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.darker_gray));
                    if (mCurrent_Video != 0)
                        mCurrent_Video--;
                    player.loadVideos(youtubes_id, mCurrent_Video, youtubes_id.size() - 1);
                    mPlayerTitleTextView.setText(youtubes_title.get(mCurrent_Video));
                    Log.e("onPrevious", mCurrent_Video + " : " + youtubes_title.get(mCurrent_Video));
                }

                @Override
                public void onNext() {
//                    mPlaylist.get(mCurrent_Video).setTextColor(getResources().getColor(R.color.darker_gray));
                    if (mCurrent_Video != youtubes_id.size() - 1)
                        mCurrent_Video++;
                    player.loadVideos(youtubes_id, mCurrent_Video, youtubes_id.size() - 1);
                    mPlayerTitleTextView.setText(youtubes_title.get(mCurrent_Video));
                    Log.e("onNext", mCurrent_Video + " : " + youtubes_title.get(mCurrent_Video));
                }

                @Override
                public void onPlaylistEnded() {
                    if (mCurrent_Video == youtubes_id.size() - 1) {
                        mPlayerTitleTextView.setText("모든 노래를 플레이하였습니다.");
                    }
                    Log.e("onPlaylistEnded", mCurrent_Video + " : " + youtubes_title.get(mCurrent_Video));
                }
            });

            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {
                    mPlayerPlayButtonView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {
                    mPlayerTitleTextView.setText("광고 중 입니다. 잠시만 기다려주세요.");
                    mPlayerPrevButtonView.setVisibility(View.INVISIBLE);
                    mPlayerPlayButtonView.setVisibility(View.INVISIBLE);
                    mPlayerNextButtonView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoStarted() {

                    getSongFragment();

                    mPlayerTitleTextView.setText(youtubes_title.get(mCurrent_Video));
                    if (mCurrent_Video != 0)
                        mPlayerPrevButtonView.setVisibility(View.VISIBLE);
                    else
                        mPlayerPrevButtonView.setVisibility(View.INVISIBLE);

                    mPlayerPlayButtonView.setVisibility(View.VISIBLE);
                    mPlayerPlayButtonView.setImageDrawable(getResources().getDrawable(R.drawable.icon_player_pause));

                    if (mCurrent_Video != youtubes_id.size() - 1)
                        mPlayerNextButtonView.setVisibility(View.VISIBLE);
                    else
                        mPlayerNextButtonView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoEnded() {
                    if(mCurrent_Video == 0 && !player.hasNext()) {
                        mCurrent_Video++;
                        player.loadVideos(youtubes_id, mCurrent_Video, youtubes_id.size() - 1);
                        mPlayerTitleTextView.setText(youtubes_title.get(mCurrent_Video));
                    }
                    Log.e("onVideoEnded", mCurrent_Video + " : " + youtubes_title.get(mCurrent_Video));
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {
                    Log.e("onError", mCurrent_Video + " : " + youtubes_title.get(mCurrent_Video));
                }
            });
        }
    }

    private void getSongFragment() {

        getIntent().putExtra("song_id", song_id.get(mCurrent_Video));

        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down)
                .add(R.id.filterFragment_Container, SongFragment
                                .instantiate(this, SongFragment.class.getName()),
                        LIST_FRAGMENT_TAG
                ).addToBackStack(null).commit();
    }

    private void getArtistFragment() {

        getIntent().putExtra("spotifyArtistID", SPOTIFY_ARTIST_ID);

        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down)
                .add(R.id.filterFragment_Container, ArtistFragment
                                .instantiate(this, ArtistFragment.class.getName()),
                        LIST_FRAGMENT_TAG
                ).addToBackStack(null).commit();
    }

    private void getAlbumFragment() {

        getIntent().putExtra("spotifyAlbumID", SPOTIFY_ALBUM_ID);

        Fragment f = getFragmentManager().findFragmentByTag(LIST_FRAGMENT_TAG);
        if (f != null) {
            getFragmentManager().popBackStack();
        }

        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up,
                        R.anim.slide_down,
                        R.anim.slide_up,
                        R.anim.slide_down)
                .add(R.id.filterFragment_Container, AlbumFragment
                                .instantiate(this, AlbumFragment.class.getName()),
                        LIST_FRAGMENT_TAG
                ).addToBackStack(null).commit();
    }

}
