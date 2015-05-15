package com.myprescience.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.ui.album.MyAlbumListActivity;
import com.myprescience.ui.artist.MyArtistListActivity;
import com.myprescience.ui.song.MySongListActivity;
import com.myprescience.util.Indicator;
import com.myprescience.util.InsertUpdateQuery;
import com.myprescience.util.LocalMusicSyncThread;
import com.myprescience.util.RecommendThread;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.myprescience.util.Server.EXEC_RECOMMEND_ALGORITHM;
import static com.myprescience.util.Server.INSERT_LOCAL_FILE_RATING;
import static com.myprescience.util.Server.RATING_API;
import static com.myprescience.util.Server.RECOMMEND_API;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_USER;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyPageActivity extends ActionBarActivity {

    private UserData userDTO;
    private GridView gridView;
    private int userId;
    private boolean mLockListView = false;

    private LinearLayout mMySongButton, mMyAlbumButton, mMp3SyncButton, mMyArtistButton;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setActionBar(R.string.title_section4);
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);

//        facebook_profile = (ImageView) findViewById(R.id.profileImageView);
//        facebook_profile.setImageDrawable(getFACEBOOK_PROFILE_BITMAP());

        mMySongButton = (LinearLayout) findViewById(R.id.mySongButton);
        mMySongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MySongListActivity.class);
                startActivity(intent);
            }
        });

        mMyAlbumButton = (LinearLayout) findViewById(R.id.myAlbumButton);
        mMyAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyAlbumListActivity.class);
                startActivity(intent);
            }
        });

        mMp3SyncButton = (LinearLayout) findViewById(R.id.mp3SyncButton);
        mMp3SyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncLocalMusicFile();
            }
        });

        mMyArtistButton = (LinearLayout) findViewById(R.id.myArtistButton);
        mMyArtistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MyArtistListActivity.class);
                startActivity(intent);
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

    private void syncLocalMusicFile() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MyPageActivity.this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String[] songFile = {
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Video.Media.ARTIST };
                Cursor musiccursor = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        songFile, null, null, null);

                Log.e("CursorCount", musiccursor.getCount() + "");
                while(musiccursor.moveToNext()) {
                    int music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
                    String title = musiccursor.getString(music_column_index);

                    music_column_index = musiccursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
                    String artist = musiccursor.getString(music_column_index);

//                    try {
                        Log.e("MP3", title + " " + artist);
//                        new LocalMusicSyncThread(getApplicationContext(), SERVER_ADDRESS+RATING_API+INSERT_LOCAL_FILE_RATING
//                                +WITH_USER+userDTO.getId()+"&title="+URLEncoder.encode(title,"utf-8")+"&artist="+URLEncoder.encode(artist,"utf-8") ).start();
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                }

                //                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        public void run() {
//                            new RecommendThread(getApplicationContext(), SERVER_ADDRESS + RECOMMEND_API + EXEC_RECOMMEND_ALGORITHM + WITH_USER + userDTO.getId()).start();
//                        }
//                    }, 5000);
                dialog.dismiss();     //닫기
            }
        });
        alert.setMessage("보유하고 계신 음악을 동기화합니다.");
        alert.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
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
        }
        return super.onOptionsItemSelected(item);
    };

}