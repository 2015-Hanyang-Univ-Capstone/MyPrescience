package com.myprescience.ui.mix_play;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class PlayerActivity extends YouTubeBaseActivity {

    private UserData userDTO;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_player);
        setActionBar();
        userDTO = new UserData(getApplicationContext());

        mIndicator = new Indicator(this);

    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        setTitle("");

//        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
//        TitleTextView.setText(title);
//        Typeface face = Typeface.createFromAsset(getAssets(),
//                "Steinerlight.ttf");
//        TitleTextView.setTypeface(face);

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

}
