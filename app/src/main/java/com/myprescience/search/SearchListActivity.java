package com.myprescience.search;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;
import com.myprescience.util.LocalMusicSyncThread;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;

import java.util.ArrayList;
import java.util.List;

import static com.myprescience.util.Server.LUCENE_API;
import static com.myprescience.util.Server.SEARCH_SONGS;
import static com.myprescience.util.Server.SERVER_ADDRESS;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class SearchListActivity extends ActionBarActivity {

    private UserData userDTO;

    private ListView mSearchListView;
    private SearchListAdapter mSearchListAdapter;
    private boolean mLockListView = false;

    private int mListCount;
    private int mListAddCount;
    private int totalListSize;
    private JSONArray mAlbumArray;

    private EditText mInputQuery;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        userDTO = new UserData(getApplicationContext());
        setActionbar();

        mListCount = 0;
        mListAddCount = 5;
        mIndicator = new Indicator(this);

        mInputQuery = (EditText) findViewById(R.id.toolbar_search);

        TextWatcher watcher = new TextWatcher()
        {
            @Override
            public void afterTextChanged(Editable s) {
                //텍스트 변경 후 발생할 이벤트를 작성.
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
                //텍스트의 길이가 변경되었을 경우 발생할 이벤트를 작성.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                //텍스트가 변경될때마다 발생할 이벤트를 작성.
                if (mInputQuery.isFocusable())
                {
                    String input = mInputQuery.getText().toString();
                    Log.e("textChange", input);
                    List<NameValuePair> parameters = new ArrayList<>();
                    parameters.add(new BasicNameValuePair("q", input));
                    new LocalMusicSyncThread(getApplicationContext(), SERVER_ADDRESS + LUCENE_API + SEARCH_SONGS, parameters).start();
                }
            }
        };

        //호출
        mInputQuery.addTextChangedListener(watcher);

        mSearchListView = (ListView) findViewById(R.id.searchListView);

        mSearchListAdapter = new SearchListAdapter(this, userDTO.getId());
        mSearchListView.setAdapter(mSearchListAdapter);

//        new getSimpleSongTask().execute(SERVER_ADDRESS+ARTIST_API+SELECT_MY_ARTISTS+WITH_USER+userDTO.getId());

        mSearchListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 현재 가장 처음에 보이는 셀번호와 보여지는 셀번호를 더한값이
                // 전체의 숫자와 동일해지면 가장 아래로 스크롤 되었다고 가정
                if ( (totalItemCount+mListAddCount < totalListSize) && ((firstVisibleItem + visibleItemCount) == totalItemCount) && (mLockListView == false) ) {
                    mListCount += mListAddCount;
//                        else if(totalItemCount+10 > totalListSize && !(totalItemCount >= totalListSize))
//                            mListCount = totalListSize - (10+1);
                    mLockListView = true;
                } else if(totalItemCount + mListAddCount >= totalListSize && totalListSize > 9) {
                    mListCount += mListAddCount;
                    mListAddCount =  totalListSize - mListCount;
                    mSearchListView.setOnScrollListener(null);
                }
            }
        });
    }

    public void setActionbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("key pressed", String.valueOf(event.getKeyCode()));
        return super.dispatchKeyEvent(event);
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