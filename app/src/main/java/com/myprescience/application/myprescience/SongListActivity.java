package com.myprescience.application.myprescience;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 곡 리스트 출력하는 액티비티
 */


public class SongListActivity extends Activity {
    public static Activity sSonglistActivity;
    // 추천 받을 최소 곡 수
    public static int MIN_SELECTED_SONG = 5;

    private int selectCount = 0;
    private ImageButton rightButton;
    private TextView textView;
    private ListView songListView;
    private SongListAdapter songListAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sSonglistActivity = SongListActivity.this;
                Intent intent = new Intent(SongListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        textView = (TextView) findViewById(R.id.top);
        textView.setText("최소 "+ MIN_SELECTED_SONG +"곡 이상 평가해주세요.");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        songListAdapter = new SongListAdapter(this, selectCount, progressBar, textView, rightButton);
        songListView = (ListView) findViewById(R.id.songListView);
        songListView.setAdapter(songListAdapter);
        for(int i=0; i<50; i++)
            songListAdapter.addItem(null,i+"",i+"",0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_song_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
